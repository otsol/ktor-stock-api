# ktor-stock-api
Made by Otso Luukkanen
GitHub: https://github.com/otsol/ktor-stock-api

# How to test locally
git clone https://github.com/otsol/ktor-stock-api.git  
cd ktor-stock-api  
./gradlew jar  
cd build/libs/  
java -jar stock-api-0.0.1.jar  

# test the running application
curl http://0.0.0.0:8080/stock/AAPL
