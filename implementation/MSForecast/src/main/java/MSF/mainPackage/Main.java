package MSF.mainPackage;

import MSF.forecast.ForecastController;

public class Main {
    public static void main(String[] args) {
        ForecastController forecastController = new ForecastController();
        forecastController.startForecast();
    }


}
