import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApp {
    private static JFrame frame;
    private static JTextField locationField;
    private static JButton fetchButton;
    private static JTextArea weatherDisplay;
    private static final String apiKey = "1e655b91ad63d14c707ecbc15f3e0d40";

    private static String fetchWeatherData(String city) {
        try {
            String query = String.format("q=%s&appid=%s", city, apiKey);
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?" + query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject jsonObject = (JSONObject) JSONValue.parse(response.toString());
            JSONObject mainObj = (JSONObject) jsonObject.get("main");

            double temperatureKelvin = (double) mainObj.get("temp");
            long humidity = (long) mainObj.get("humidity");

            // Convert temperature from Kelvin to Celsius
            double temperatureCelsius = temperatureKelvin - 273.15;

            // Retrieve weather description
            JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
            JSONObject weather = (JSONObject) weatherArray.get(0);
            String description = (String) weather.get("description");

            return "Description: " + description + "\nTemperature: " +
                    String.format("%.2f", temperatureCelsius) + " °C\nHumidity: " + humidity + "%";

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to fetch weather data";
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("Weather Forecasting App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new FlowLayout());

        locationField = new JTextField(15);
        fetchButton = new JButton("Fetch Weather");
        weatherDisplay = new JTextArea(10, 30);
        weatherDisplay.setEditable(false);

        frame.add(new JLabel("Enter City Name:"));
        frame.add(locationField);
        frame.add(fetchButton);
        frame.add(new JScrollPane(weatherDisplay));

        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = locationField.getText();
                String weatherInfo = fetchWeatherData(city);
                weatherDisplay.setText(weatherInfo);
            }
        });

        frame.setVisible(true);
    }
}
