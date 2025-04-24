@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  erp-api-ktor startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and ERP_API_KTOR_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Dio.ktor.development=false"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\erp-api-ktor-0.0.1.jar;%APP_HOME%\lib\ktor-server-netty-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-server-config-yaml-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-server-auth-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-server-content-negotiation-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-server-cors-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-server-sessions-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-server-core-jvm-3.1.1.jar;%APP_HOME%\lib\kotlin-reflect-2.1.10.jar;%APP_HOME%\lib\ktor-serialization-kotlinx-json-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-serialization-kotlinx-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-client-core-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-websocket-serialization-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-serialization-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-events-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-http-cio-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-websockets-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-sse-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-http-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-network-jvm-3.1.1.jar;%APP_HOME%\lib\ktor-utils-jvm-3.1.1.jar;%APP_HOME%\lib\kotlinx-coroutines-slf4j-1.10.1.jar;%APP_HOME%\lib\ktor-io-jvm-3.1.1.jar;%APP_HOME%\lib\kotlinx-coroutines-core-jvm-1.10.1.jar;%APP_HOME%\lib\yamlkt-jvm-0.13.0.jar;%APP_HOME%\lib\kotlinx-serialization-core-jvm-1.8.0.jar;%APP_HOME%\lib\kotlinx-serialization-json-io-jvm-1.8.0.jar;%APP_HOME%\lib\kotlinx-serialization-json-jvm-1.8.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.8.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.8.0.jar;%APP_HOME%\lib\kotlinx-io-core-jvm-0.6.0.jar;%APP_HOME%\lib\kotlinx-io-bytestring-jvm-0.6.0.jar;%APP_HOME%\lib\kotlin-stdlib-2.1.20.jar;%APP_HOME%\lib\logback-classic-1.4.14.jar;%APP_HOME%\lib\sqlite-jdbc-3.41.2.1.jar;%APP_HOME%\lib\jaxws-tools-3.0.0.jar;%APP_HOME%\lib\jaxws-eclipselink-plugin-3.0.0.jar;%APP_HOME%\lib\sdo-eclipselink-plugin-3.0.0.jar;%APP_HOME%\lib\jaxws-rt-3.0.0.jar;%APP_HOME%\lib\release-documentation-3.0.0-docbook.zip;%APP_HOME%\lib\samples-3.0.0.zip;%APP_HOME%\lib\jakarta.xml.ws-api-3.0.0.jar;%APP_HOME%\lib\annotations-23.0.0.jar;%APP_HOME%\lib\logback-core-1.4.14.jar;%APP_HOME%\lib\slf4j-api-2.0.16.jar;%APP_HOME%\lib\org.eclipse.persistence.moxy-3.0.0-M2.jar;%APP_HOME%\lib\jaxb-jxc-3.0.0.jar;%APP_HOME%\lib\jaxb-impl-3.0.0.jar;%APP_HOME%\lib\jaxb-xjc-3.0.0.jar;%APP_HOME%\lib\jaxb-core-3.0.0.jar;%APP_HOME%\lib\jakarta.xml.bind-api-3.0.0.jar;%APP_HOME%\lib\saaj-impl-2.0.0.jar;%APP_HOME%\lib\jakarta.xml.soap-api-2.0.0.jar;%APP_HOME%\lib\jakarta.jws-api-3.0.0.jar;%APP_HOME%\lib\jakarta.mail-2.0.0.jar;%APP_HOME%\lib\jakarta.activation-2.0.0.jar;%APP_HOME%\lib\jakarta.annotation-api-2.0.0.jar;%APP_HOME%\lib\config-1.4.3.jar;%APP_HOME%\lib\jansi-2.4.1.jar;%APP_HOME%\lib\netty-codec-http2-4.1.118.Final.jar;%APP_HOME%\lib\alpn-api-1.1.3.v20160715.jar;%APP_HOME%\lib\netty-transport-native-kqueue-4.1.118.Final.jar;%APP_HOME%\lib\netty-transport-native-epoll-4.1.118.Final.jar;%APP_HOME%\lib\ha-api-3.1.12.jar;%APP_HOME%\lib\gmbal-api-only-4.0.2.jar;%APP_HOME%\lib\management-api-3.2.3.jar;%APP_HOME%\lib\streambuffer-2.0.1.jar;%APP_HOME%\lib\stax-ex-2.0.0.jar;%APP_HOME%\lib\mimepull-1.9.13.jar;%APP_HOME%\lib\FastInfoset-2.0.0.jar;%APP_HOME%\lib\woodstox-core-6.2.1.jar;%APP_HOME%\lib\stax2-api-4.2.1.jar;%APP_HOME%\lib\org.eclipse.persistence.sdo-3.0.0-M2.jar;%APP_HOME%\lib\commonj.sdo-2.1.1.jar;%APP_HOME%\lib\netty-codec-http-4.1.118.Final.jar;%APP_HOME%\lib\netty-handler-4.1.118.Final.jar;%APP_HOME%\lib\netty-codec-4.1.118.Final.jar;%APP_HOME%\lib\netty-transport-classes-kqueue-4.1.118.Final.jar;%APP_HOME%\lib\netty-transport-classes-epoll-4.1.118.Final.jar;%APP_HOME%\lib\netty-transport-native-unix-common-4.1.118.Final.jar;%APP_HOME%\lib\netty-transport-4.1.118.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.118.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.118.Final.jar;%APP_HOME%\lib\netty-common-4.1.118.Final.jar;%APP_HOME%\lib\org.eclipse.persistence.asm-3.0.0-M2.jar;%APP_HOME%\lib\org.eclipse.persistence.core-3.0.0-M2.jar


@rem Execute erp-api-ktor
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %ERP_API_KTOR_OPTS%  -classpath "%CLASSPATH%" io.ktor.server.netty.EngineMain %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable ERP_API_KTOR_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%ERP_API_KTOR_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
