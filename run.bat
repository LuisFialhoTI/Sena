@echo off
echo ========================================================
echo             INICIANDO COMPILACAO DO SENA
echo ========================================================
javac -cp "backend/lib/*" backend/model/*.java backend/exception/*.java backend/database/*.java backend/controller/*.java backend/gui/*.java backend/SENAWeb.java

if %errorlevel% neq 0 (
    echo.
    echo [ERRO] Falha na compilacao do Java. Verifique os erros acima.
    pause
    exit /b %errorlevel%
)

echo.
echo ========================================================
echo             INICIANDO O SERVIDOR E PAINEL SWING
echo ========================================================
java -cp "backend/lib/*;backend" SENAWeb
