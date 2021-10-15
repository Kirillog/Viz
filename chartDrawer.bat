@echo off
set argC=0
for %%x in (%*) do Set /A argC+=1
if %argC%==1 (gradlew -q --console=plain run --args='%1') else (gradlew -q --console=plain run)