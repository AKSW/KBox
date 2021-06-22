package org.aksw.kbox.kibe;

import org.aksw.kbox.kns.KN;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONSerializer {

    private static JSONSerializer JSONSerializerInstance;
    private final static String OUTPUT = "-o";
    private final static String JSON_OUTPUT_FORMAT = "json";
    private final static int JSON_INDENTATION = 4;

    // STATUS CODES
    public final static int OK = 200;
    public final static int INTERNAL_SERVER_ERROR = 500;

    private boolean isJsonOutput;
    private final List<String> visitedKNList;
    private KN visitedKN; // to be used with the -info command

    private JSONSerializer() {
        visitedKNList = new ArrayList<>();
    }

    public static JSONSerializer getInstance() {
        if (JSONSerializerInstance == null) {
            synchronized (JSONSerializer.class) {
                if (JSONSerializerInstance == null) {
                    JSONSerializerInstance = new JSONSerializer();
                }
            }
        }
        return JSONSerializerInstance;
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

    public void addVisitedKN(String kn) {
        visitedKNList.add(kn);
    }

    private void printJsonResponse(int status_code, String msg, JSONArray results) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status_code", status_code);
        jsonObject.put("message",  msg);
        jsonObject.put("results",  results);
        System.out.println(jsonObject.toString(JSON_INDENTATION));
    }

    public void printInstallCommandJsonResponse(String message) {
        if (!isJsonOutput) {
            return;
        }
        JSONArray results = new JSONArray();
        results.put(true);
        printJsonResponse(JSONSerializer.OK, message, results);
    }

    public void printVisitedKNAsJsonResponse(String message) {
        if (!isJsonOutput) {
            return;
        }
        JSONArray visitedKNs = new JSONArray();
        for (String kn : visitedKNList) {
            String[] split = kn.split(",");
            JSONObject jsonKN = new JSONObject();
            jsonKN.put("name", split[0]);
            jsonKN.put("format", split[1]);
            jsonKN.put("version", split[2]);
            visitedKNs.put(jsonKN);
        }
        printJsonResponse(JSONSerializer.OK, message, visitedKNs);
        visitedKNList.clear(); // clear all the visited list.
    }

    public void printKnsListJsonFormat(List<String> knsList, String message) {
        if (!isJsonOutput) {
            return;
        }
        JSONArray results = new JSONArray();
        for (String url : knsList) {
            results.put(url);
        }
        printJsonResponse(JSONSerializer.OK, message, results);
    }

    public void printRemoveCommandJsonResponse(String message) {
        if(!isJsonOutput) {
            return;
        }
        JSONArray results = new JSONArray();
        results.put(true);
        printJsonResponse(JSONSerializer.OK, message, results);
    }

    public void printResourceDirectoryAsJsonResponse(String message, String path) {
        if (!isJsonOutput) {
            return;
        }
        JSONArray results = new JSONArray();
        results.put(path);
        printJsonResponse(JSONSerializer.OK, message, results);
    }

    public void printKNInfoAsJsonResponse(String message) {
        if (!isJsonOutput) {
            return;
        }
        JSONArray results = new JSONArray();
        if (visitedKN != null) {
            results.put(visitedKN.getJsonObject());
        }
        printJsonResponse(JSONSerializer.OK, message, results);
    }

    public void printLocateCommandJsonResponse(String message, String path) {
        if (!isJsonOutput) {
            return;
        }
        JSONArray results = new JSONArray();
        results.put(path);
        printJsonResponse(JSONSerializer.OK, message, results);
    }

    public void printKBoxVersionAsJsonResponse(String message, String version) {
        JSONArray results = new JSONArray();
        results.put(version);
        printJsonResponse(JSONSerializer.OK, message, results);
    }

    public void printErrorInJsonFormat(String errorMessage, boolean isEmptyResult) {
        if (!isJsonOutput) {
            return;
        }
        JSONArray results = new JSONArray();
        if (!isEmptyResult) {
            results.put(false);
        }
        printJsonResponse(INTERNAL_SERVER_ERROR, errorMessage, results);
    }

    public void setVisitedKN(KN visitedKN) {
        this.visitedKN = visitedKN;
    }

    public boolean getIsJsonOutput() {
        return isJsonOutput;
    }


}
