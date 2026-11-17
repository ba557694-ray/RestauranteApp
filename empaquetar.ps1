$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $raiz

$clases = Join-Path $raiz "out\clases"
$contenidoJar = Join-Path $raiz "out\jar"
$jarFinal = Join-Path $raiz "RestauranteApp.jar"
$libreriaSqlite = Join-Path $raiz "lib\sqlite-jdbc-3.54.1.0.jar"

if (-not (Test-Path $libreriaSqlite)) {
    throw "No se encontró $libreriaSqlite"
}

if (Test-Path $clases) {
    Remove-Item $clases -Recurse -Force
}
New-Item -ItemType Directory -Path $clases -Force | Out-Null

$fuentes = Get-ChildItem -Path (Join-Path $raiz "src") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 --release 17 -cp $libreriaSqlite -d $clases $fuentes
if ($LASTEXITCODE -ne 0) {
    throw "Falló la compilación del proyecto"
}

if (Test-Path $contenidoJar) {
    Remove-Item $contenidoJar -Recurse -Force
}
New-Item -ItemType Directory -Path $contenidoJar -Force | Out-Null

Copy-Item (Join-Path $clases "*") $contenidoJar -Recurse -Force

Push-Location $contenidoJar
try {
    & jar xf $libreriaSqlite
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo extraer el controlador SQLite"
    }
} finally {
    Pop-Location
}

$manifiesto = Join-Path $contenidoJar "MANIFEST.MF"
$lineas = @(
    "Manifest-Version: 1.0",
    "Main-Class: vista.Principal",
    "Implementation-Title: Sistema de Gestion de Restaurante",
    "Implementation-Version: 1.0",
    ""
)
Set-Content -Path $manifiesto -Value $lineas -Encoding ASCII

if (Test-Path $jarFinal) {
    Remove-Item $jarFinal -Force
}

Push-Location $contenidoJar
try {
    & jar cfm $jarFinal $manifiesto .
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo generar el JAR final"
    }
} finally {
    Pop-Location
}

Write-Host "JAR generado en $jarFinal"
Write-Host "Contenido del controlador SQLite incluido en el JAR"
Write-Host "Ejecute: java -jar RestauranteApp.jar"
