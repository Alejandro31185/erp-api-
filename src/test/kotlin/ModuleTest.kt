import io.ktor.client.request.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ModuleTest {

    @Test
    fun testGetApiClientes() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.get("/api/clientes").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiClientes() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.post("/api/clientes").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiDisponibilidades() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.get("/api/disponibilidades").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiDisponibilidades() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.post("/api/disponibilidades").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiFacturasCompra() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.get("/api/facturas/compra").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiFacturasCompra() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.post("/api/facturas/compra").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiFacturasVenta() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.get("/api/facturas/venta").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiFacturasVenta() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.post("/api/facturas/venta").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiProveedores() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.get("/api/proveedores").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiProveedores() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.post("/api/proveedores").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiReportesVentasvscompras() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.get("/api/reportes/ventas-vs-compras").apply {
            TODO("Please write your test here")
        }
    }
}