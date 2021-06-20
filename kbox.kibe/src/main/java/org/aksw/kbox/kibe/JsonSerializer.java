package org.aksw.kbox.kibe;

import org.aksw.kbox.kns.KN;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonSerializer {

    private static JsonSerializer jsonSerializerInstance;
    private final static String OUTPUT = "-o";
    private final static String JSON_OUTPUT_FORMAT = "json";
    private final static int JSON_INDENTATION = 4;
    private static final List<String> visitedKNList = new ArrayList<>();

    private boolean isJsonOutput;

    private JsonSerializer() {

    }

    public static JsonSerializer getInstance() {
        if (jsonSerializerInstance == null) {
            synchronized (JsonSerializer.class) {
                if (jsonSerializerInstance == null) {
                    jsonSerializerInstance = new JsonSerializer();
                }
            }
        }
        return jsonSerializerInstance;
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

    public void printVisitedKNJsonFormat() {
        if (!isJsonOutput) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        JSONArray visitedKNs = new JSONArray();
        for (String kn : visitedKNList) {
            String[] split = kn.split(",");
            JSONObject jsonKN = new JSONObject();
            jsonKN.put("name", split[0]);
            jsonKN.put("format", split[1]);
            jsonKN.put("version", split[2]);
            visitedKNs.put(jsonKN);
        }
        jsonObject.put("results", visitedKNs);
        System.out.println(jsonObject.toString(JSON_INDENTATION));
        visitedKNList.clear(); // clear all the visited list.
    }

    public void printKNInfo(KN kn) {
        JSONObject knJsonObj = new JSONObject();
        knJsonObj.put("success", true);
        knJsonObj.put("info", kn.getJsonObject());
        System.out.println(knJsonObj.toString(JSON_INDENTATION));
    }

    public void addVisitedKN(String kn) {
        visitedKNList.add(kn);
    }

    public boolean getIsJsonOutput() {
        return isJsonOutput;
    }

}
