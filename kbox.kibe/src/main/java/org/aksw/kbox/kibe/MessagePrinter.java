package org.aksw.kbox.kibe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessagePrinter {

    private static MessagePrinter messagePrinterInstance;
    private final static String OUTPUT = "-o";
    private final static String JSON_OUTPUT_FORMAT = "json";
    private final static int JSON_INDENTATION = 4;
    private static final List<String> visitedKNList = new ArrayList<>();

    private boolean isJsonOutput;

    private MessagePrinter() {

    }

    public static MessagePrinter getInstance() {
        if (messagePrinterInstance == null) {
            synchronized (MessagePrinter.class) {
                if (messagePrinterInstance == null) {
                    messagePrinterInstance = new MessagePrinter();
                }
            }
        }
        return messagePrinterInstance;
    }

    public void containsJsonOutputCommand(Map<String, String[]> commands) {
        if (commands.containsKey(OUTPUT)) {
            if (commands.get(OUTPUT)[0] == null) {
                System.out.println("Output format not specified. Hence showing default outputs.");
                isJsonOutput = false;
            }
            boolean result = commands.get(OUTPUT)[0].equalsIgnoreCase(JSON_OUTPUT_FORMAT);
            commands.remove(OUTPUT); // removing the -o from map, so it won't affect to other if conditions
            isJsonOutput = result;
        }
        isJsonOutput = false;
    }

    public void printOutput(String msg) {
        if (!isJsonOutput) {
            System.out.println(msg);
        }
    }

    public void printActionStatusInJson(String action, boolean success) {
        if (!isJsonOutput) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(action, success);
        System.out.println(jsonObject.toString(JSON_INDENTATION));
    }

    public void printPathInJsonFormat(String path) {
        if (!isJsonOutput) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("path", path);
        System.out.println(jsonObject.toString(JSON_INDENTATION));
    }

    public void printKnsListJsonFormat(List<String> knsList, boolean success) {
        if (!isJsonOutput) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (success) {
            for (String url : knsList) {
                jsonArray.put(url);
            }
        }
        jsonObject.put("success", success);
        jsonObject.put("knsList", jsonArray);
        System.out.println(jsonObject.toString(JSON_INDENTATION));
    }

}
