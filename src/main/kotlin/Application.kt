

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.sql.DriverManager
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@Serializable data class VentasPendientesReport(val grafico: List<GraficoVentaPendiente>, val tabla: List<FacturaPendienteVenta>)
@Serializable data class GraficoVentaPendiente(val cliente: String, val total: Double, val dias: Int)
@Serializable data class FacturaPendienteVenta(val cliente: String, val fecha: String, val descripcion: String, val total: Double, val dias: Int)
@Serializable data class Cliente(val id: Int? = null, val nombre: String)
@Serializable data class Proveedor(val id: Int? = null, val nombre: String)
@Serializable data class FacturaCompra(val id: Int? = null, val fecha: String, val proveedor: String, val descripcion: String, val cantidad: Int, val unitario: Double, val subtotal: Double, val iva: String, val total: Double, val estado: String)
@Serializable data class FacturaPendiente(val proveedor: String, val fecha: String, val descripcion: String, val total: Double, val dias: Int)
@Serializable data class DisponibilidadesReport(val tipo: String, val total: Double)
@Serializable data class GraficoFacturaPendiente(val proveedor: String, val total: Double, val dias: Int)
@Serializable data class FacturasPendientesReport(val grafico: List<GraficoFacturaPendiente>, val tabla: List<FacturaPendiente>)
@Serializable data class VentasVsComprasReport(val labels: List<String>, val ventas: List<Double>, val compras: List<Double>, val ventasPendientes: List<Double>, val comprasPendientes: List<Double>)
@Serializable data class IvaMensualReport(val mes: String, val debito: Double, val credito: Double)
@Serializable data class DiferidoTimeline(val fecha: String, val total: Double, val diasRestantes: Int)
@Serializable data class DisponibilidadesExtendido(val totales: List<DisponibilidadesReport>, val diferidoTotal: Double, val timeline: List<DiferidoTimeline>, val detalle: List<MovimientoDisponibilidad>)  // ✅ Este campo es clave)
@Serializable data class MovimientoDisponibilidad(val tipo: String, val banco: String, val fechaCobro: String, val monto: Double, val descripcion: String, val dias: Int)

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {  // Escucha en todas las interfaces de red
        module()
    }.start(wait = true)
}

fun mesEnPalabra(fecha: String): String {
    val partes = fecha.split("-")
    val mes = partes[1].toInt()
    val año = partes[0]
    val nombreMes = Month.of(mes).getDisplayName(TextStyle.FULL, Locale("es"))
    return "${nombreMes.replaceFirstChar { it.uppercaseChar() }} $año"
}

fun Application.module() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }

    install(ContentNegotiation) {
        json()
    }

    val dbPath = "C:/Users/Usuario/erp-api-ktor/src/main/kotlin/erp.sqlite"
    val connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")

    routing {
        // ... otros endpoints ...

        routing {
            // 🧾 Reporte: Ventas vs Compras
            get("/api/reportes/ventas-vs-compras") {
                val desde = call.request.queryParameters["desde"]
                val hasta = call.request.queryParameters["hasta"]

                // Verifica que las fechas sean las esperadas
                println("Fechas recibidas desde: $desde hasta: $hasta")

                val stmt = connection.createStatement()

                val ventas = mutableMapOf<String, Double>()
                val compras = mutableMapOf<String, Double>()
                val ventasPend = mutableMapOf<String, Double>()
                val comprasPend = mutableMapOf<String, Double>()

                val rsVentas = stmt.executeQuery("""
                    SELECT strftime('%Y-%m', fecha) as mes, SUM(total) as total, estado
                    FROM facturas_clientes
                    WHERE fecha BETWEEN '$desde' AND '$hasta'
                    GROUP BY mes, estado
                """)
                while (rsVentas.next()) {
                    val mes = rsVentas.getString("mes")
                    val total = rsVentas.getDouble("total")
                    val estado = rsVentas.getString("estado").lowercase()
                    ventas[mes] = (ventas[mes] ?: 0.0) + total
                    if (estado == "pendiente") {
                        ventasPend[mes] = (ventasPend[mes] ?: 0.0) + total
                    }
                }

                val rsCompras = stmt.executeQuery("""
                    SELECT strftime('%Y-%m', fecha) as mes, SUM(total) as total, estado
                    FROM facturas_proveedores
                    WHERE fecha BETWEEN '$desde' AND '$hasta'
                    GROUP BY mes, estado
                """)
                while (rsCompras.next()) {
                    val mes = rsCompras.getString("mes")
                    val total = rsCompras.getDouble("total")
                    val estado = rsCompras.getString("estado").lowercase()
                    compras[mes] = (compras[mes] ?: 0.0) + total
                    if (estado == "pendiente") {
                        comprasPend[mes] = (comprasPend[mes] ?: 0.0) + total
                    }
                }

                val meses = (ventas.keys + compras.keys).toSortedSet()

                // Agrega logs para verificar los datos obtenidos
                println("Ventas: $ventas")
                println("Compras: $compras")
                println("Ventas Pendientes: $ventasPend")
                println("Compras Pendientes: $comprasPend")

                call.respond(
                    VentasVsComprasReport(
                        labels = meses.toList(),
                        ventas = meses.map { ventas[it] ?: 0.0 },
                        compras = meses.map { compras[it] ?: 0.0 },
                        ventasPendientes = meses.map { ventasPend[it] ?: 0.0 },
                        comprasPendientes = meses.map { comprasPend[it] ?: 0.0 }
                    )
                )
            }

            // 📊 Reporte: IVA Mensual
            get("/api/reportes/iva-mensual") {
                val desde = call.request.queryParameters["desde"]
                val hasta = call.request.queryParameters["hasta"]
                val stmt = connection.createStatement()

                val rsVentas = stmt.executeQuery("""
                    SELECT strftime('%Y-%m', fecha) as mes, SUM(CAST(iva AS REAL)) as debito
                    FROM facturas_clientes
                    WHERE fecha BETWEEN '$desde' AND '$hasta'
                    GROUP BY mes
                """)
                val debitos = mutableMapOf<String, Double>()
                while (rsVentas.next()) {
                    debitos[rsVentas.getString("mes")] = rsVentas.getDouble("debito")
                }

                val rsCompras = stmt.executeQuery("""
                    SELECT strftime('%Y-%m', fecha) as mes, SUM(CAST(iva AS REAL)) as credito
                    FROM facturas_proveedores
                    WHERE fecha BETWEEN '$desde' AND '$hasta'
                    GROUP BY mes
                """)
                val creditos = mutableMapOf<String, Double>()

                while (rsCompras.next()) {
                    creditos[rsCompras.getString("mes")] = rsCompras.getDouble("credito")
                }

                val meses = (debitos.keys + creditos.keys).toSortedSet()

                val resultado = meses.map { mesKey ->
                    IvaMensualReport(
                        mes = mesEnPalabra(mesKey),
                        debito = debitos[mesKey] ?: 0.0,
                        credito = creditos[mesKey] ?: 0.0
                    )
                }

                println("▶️ IVA mensual generado: $resultado")
                call.respond(resultado)
            }

            /// ✅ DISPONIBILIDADES
            get("/api/reportes/disponibilidades") {
                try {
                    val hoy = LocalDate.now()
                    val stmt = connection.createStatement()
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val formatterShort = DateTimeFormatter.ofPattern("dd/MM/yy")

                    println("🟡 Iniciando consulta de totales...")

                    // 🎯 Totales por tipo
                    val rsTotales = stmt.executeQuery("""
                        SELECT tipo, SUM(monto) as total
                        FROM banco_disponibilidades
                        GROUP BY tipo
                    """)

                    val totales = mutableListOf<DisponibilidadesReport>()
                    while (rsTotales.next()) {
                        totales.add(
                            DisponibilidadesReport(
                                tipo = rsTotales.getString("tipo"),
                                total = rsTotales.getDouble("total")
                            )

                        )

                    }

                    println("🟡 Tipos totales recibidos: $totales")

                    // 📋 Detalle completo
                    val rsDetalle = stmt.executeQuery("""
                        SELECT tipo, banco, fecha_cobro, monto, descripcion
                        FROM banco_disponibilidades
                        ORDER BY fecha_cobro ASC
                    """)
                    val detalle = mutableListOf<MovimientoDisponibilidad>()
                    val timeline = mutableListOf<DiferidoTimeline>()
                    var totalDiferido = 0.0

                    while (rsDetalle.next()) {
                        val tipo = rsDetalle.getString("tipo")
                        val monto = rsDetalle.getDouble("monto")
                        val descripcion = rsDetalle.getString("descripcion")
                        val banco = rsDetalle.getString("banco")

                        val fechaStr = rsDetalle.getString("fecha_cobro")?.trim()
                        if (fechaStr.isNullOrEmpty()) {
                            println("⚠️ Fecha vacía, omitiendo registro tipo=$tipo, monto=$monto, desc=$descripcion")
                            continue
                        }

                        val fechaLD = try {
                            LocalDate.parse(fechaStr, formatter)
                        } catch (e: Exception) {
                            try {
                                LocalDate.parse(fechaStr) // fallback yyyy-MM-dd
                            } catch (e2: Exception) {
                                println("❌ No se pudo parsear fecha: '$fechaStr' → ${e2.message}")
                                continue
                            }
                        }

                        val dias = ChronoUnit.DAYS.between(hoy, fechaLD).toInt()

                        if (dias > 0) {
                            timeline.add(
                                DiferidoTimeline(
                                    fecha = fechaLD.format(formatterShort),
                                    total = monto,
                                    diasRestantes = dias
                                )
                            )
                            totalDiferido += monto
                        }

                        detalle.add(
                            MovimientoDisponibilidad(
                                tipo = tipo,
                                banco = banco,
                                fechaCobro = fechaLD.format(formatterShort),
                                monto = monto,
                                descripcion = descripcion,
                                dias = dias
                            )
                        )
                    }

                    println("🟢 Generando respuesta final...")

                    call.respond(
                        DisponibilidadesExtendido(
                            totales = totales,
                            diferidoTotal = totalDiferido,
                            timeline = timeline.sortedBy { it.diasRestantes },
                            detalle = detalle
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Error procesando reporte de disponibilidades.")
                }
            }

            // ✅ FACTURAS PENDIENTES
            get("/api/reportes/facturas-pendientes") {
                val hoy = LocalDate.now()
                val stmt = connection.createStatement()
                val rs = stmt.executeQuery("""
                    SELECT f.fecha, f.descripcion, f.total, f.estado, p.nombre as proveedor
                    FROM facturas_proveedores f
                    JOIN proveedores p ON f.proveedor_id = p.id
                    WHERE LOWER(f.estado) = 'pendiente'
    """)
                val lista = mutableListOf<FacturaPendiente>()
                while (rs.next()) {
                    val fecha = LocalDate.parse(rs.getString("fecha"))
                    val dias = ChronoUnit.DAYS.between(fecha, hoy).toInt()
                    lista.add(
                        FacturaPendiente(
                            proveedor = rs.getString("proveedor"),
                            fecha = rs.getString("fecha"),//
                            descripcion = rs.getString("descripcion"),
                            total = rs.getDouble("total"),
                            dias = dias
                        )
                    )
                }

                val grafico = lista.groupBy { it.proveedor }.map { (prov, facts) ->
                    GraficoFacturaPendiente(
                        proveedor = prov,
                        total = facts.sumOf { it.total },
                        dias = facts.sumOf { it.dias }
                    )
                }

                call.respond(FacturasPendientesReport(grafico, lista))
            }

            get("/api/reportes/ventas-pendientes") {
                val hoy = LocalDate.now()
                val stmt = connection.createStatement()
                val rs = stmt.executeQuery("""
                    SELECT f.fecha, f.descripcion, f.total, f.estado, c.nombre as cliente
                    FROM facturas_clientes f
                    JOIN clientes c ON f.cliente_id = c.id
                    WHERE LOWER(f.estado) = 'pendiente'
                """)
                val lista = mutableListOf<FacturaPendienteVenta>()
                while (rs.next()) {
                    val fecha = LocalDate.parse(rs.getString("fecha"))
                    val dias = ChronoUnit.DAYS.between(fecha, hoy).toInt()
                    lista.add(
                        FacturaPendienteVenta(
                            cliente = rs.getString("cliente"),
                            fecha = rs.getString("fecha"),
                            descripcion = rs.getString("descripcion"),
                            total = rs.getDouble("total"),
                            dias = dias
                        )
                    )
                }
                val grafico = lista.groupBy { it.cliente }.map { (cli, facts) ->
                    GraficoVentaPendiente(
                        cliente = cli,
                        total = facts.sumOf { it.total },
                        dias = facts.sumOf { it.dias }
                    )
                }
                call.respond(VentasPendientesReport(grafico, lista))





        }}}}
