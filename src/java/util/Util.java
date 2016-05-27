/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import modelo.Voo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author alexandre
 */
public class Util {
    
    public static JSONObject toJSON(ArrayList<Voo> list) {
        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        list.stream().forEach(jsonArray::add);
        json.put("voos", jsonArray);
        json.put("size", jsonArray.size());
        
        return json;
    }
}
