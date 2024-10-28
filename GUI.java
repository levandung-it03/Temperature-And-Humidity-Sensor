import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dtos.ThingSpeakResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class GUI {
    private static final String apiReadKey = "RSJ6MDJBWBVG6O0D";
    private static final String apiWriteKey = "X4IQSOK40JDTXKCY";
    private static final String channelId = "2718444";
    private static Timer timer;

    public static void main(String[] args) {
        //--Loading notification
        JDialog loadingDialog = GUI.getLoadingDialog("Hold your connection to load data from ThingSpeak...");

        ThingSpeakResponse channelData = GUI.fetchDataFromThingSpeak();
        boolean hasData = !Objects.isNull(channelData) && !Objects.isNull(channelData.getFeeds())
            && channelData.getFeeds().length != 0;

        //--Main Application
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());

        JPanel labelPanel = new JPanel(new FlowLayout());
        labelPanel.setPreferredSize(new Dimension(400, 40));
        JLabel label = new JLabel("DHT11 SENSOR MANAGEMENT");
        label.setFont(new Font("Consolas", Font.PLAIN, 25));
        labelPanel.add(label);

        JTextField temperature = new JTextField();
        temperature.setText(hasData ? channelData.getFeeds()[0].getField1().toString() : "");
        JPanel temperaturePanel = GUI.buildFieldSetLegend(temperature, "Temperature Threshold");

        JTextField humidity = new JTextField();
        humidity.setText(hasData ? channelData.getFeeds()[0].getField2().toString() : "");
        JPanel humidityPanel = GUI.buildFieldSetLegend(humidity, "Humidity Threshold");

        JComboBox status = new JComboBox(new String[] {"On", "Off"});
        status.setSelectedIndex((hasData && channelData.getFeeds()[0].getField3().equals(0)) ? 1 : 0);
        JPanel statusPanel = GUI.buildFieldSetLegend(status, "Speaker Status");

        JPanel submitPanel = new JPanel(new FlowLayout());
        JButton submit = new JButton("Confirm Changes");
        submit.setBackground(Color.black);
        submit.setForeground(Color.white);
        submit.setFont(new Font("Consolas", Font.PLAIN, 16));
        submit.setPreferredSize(new Dimension(250, 40));
        submitPanel.setPreferredSize(new Dimension(250, 70));
        submit.addActionListener(GUI.submitListener(temperature, humidity, status)); //--Add ActionListener
        submitPanel.add(submit);

        frame.add(labelPanel);
        frame.add(temperaturePanel);
        frame.add(humidityPanel);
        frame.add(statusPanel);
        frame.add(submitPanel);

        frame.setTitle("Temperature & Humidity Sensor Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(400, 400);
        frame.setBackground(Color.DARK_GRAY);
        frame.setLocationRelativeTo(null);

        loadingDialog.dispose();
        frame.setVisible(true);
    }

    private static JDialog getLoadingDialog(String message) {
        JLabel loadingLabel = new JLabel(message);
        loadingLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        JDialog loadingDialog = new JDialog();
        loadingDialog.setTitle("Application");
        loadingDialog.setLayout(new FlowLayout());
        loadingDialog.add(loadingLabel);
        loadingDialog.setSize(new Dimension(600, 70));
        loadingDialog.setLocationRelativeTo(null);
        loadingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loadingDialog.setVisible(true);
        return loadingDialog;
    }

    private static ActionListener submitListener(JTextField temperature, JTextField humidity, JComboBox status) {
        return (actionEvent) -> {
            if (temperature.getText().isBlank() || humidity.getText().isBlank()) {
                JOptionPane.showMessageDialog(null, "Information is not enough.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!Objects.isNull(timer) && timer.isRunning()) {
                JOptionPane.showMessageDialog(null, "Wait in 15 seconds to make new update.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double temperatureVal = Double.parseDouble(temperature.getText());
                double humidityVal = Double.parseDouble(humidity.getText());
                int speakerStatusVal = Objects.requireNonNull(status.getSelectedItem()).equals("On") ? 1 : 0;

                GUI.postDataToThingSpeak(temperatureVal, humidityVal, speakerStatusVal);
                timer = new Timer(15000, e -> {
                    timer.stop();
                });
                timer.setRepeats(false); // Ensure it doesn't repeat
                timer.start(); // Start the timer
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Information is wrong.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            }
        };
    }

    private static JPanel buildFieldSetLegend(JComponent inpField, String legend) {
        JLabel label = new JLabel(legend, JLabel.LEFT);
        label.setFont(new Font("Consolas", Font.PLAIN, 16));

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setPreferredSize(new Dimension(250, 65));

        panel.add(label);
        panel.add(inpField);
        return panel;
    }

    private static ThingSpeakResponse fetchDataFromThingSpeak() {
        try {
            String urlString = "https://api.thingspeak.com/channels/" + channelId +
                "/feeds.json?api_key=" + apiReadKey + "&results=1";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return GUI.readJson(response.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to retrieve data from ThingSpeak.");
            return null;
        }
    }

    private static void postDataToThingSpeak(Double temperature, Double humidity, Integer speakerStatus) {
        try {
            String urlString = "https://api.thingspeak.com/update.json?api_key=" + apiWriteKey;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // Enable writing to the connection

            // Create the request body
            String requestBody = "field1=" + temperature + "&field2=" + humidity + "&field3=" + speakerStatus;

            // Write the request body to the output stream
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successful post
                JOptionPane.showMessageDialog(null, "Data successfully sent to ThingSpeak!");
            } else {
                // Handle error response
                JOptionPane.showMessageDialog(null, "Failed to send data. Response code: " + responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to post data to ThingSpeak.");
        }
    }

    private static ThingSpeakResponse readJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.readValue(json, ThingSpeakResponse.class);
        } catch (JsonProcessingException e) {
            e.fillInStackTrace();
            return new ThingSpeakResponse();
        }
    }
}
