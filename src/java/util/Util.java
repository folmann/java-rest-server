/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import modelo.Hospedagem;
import modelo.Voo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author alexandre
 */
public class Util {
    
    public static JSONObject toJSONVoos(ArrayList<Voo> list) {
        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        list.stream().forEach(jsonArray::add);
        json.put("voos", jsonArray);
        json.put("size", jsonArray.size());
        
        return json;
    }
    
    public static JSONObject toJSONHospedagens(ArrayList<Hospedagem> list) {
        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        list.stream().forEach(jsonArray::add);
        json.put("hospedagens", jsonArray);
        json.put("size", jsonArray.size());
        
        return json;
    }
    
    public static ConcurrentMap<String, Hospedagem> iniciarHospedagens() {
        ConcurrentMap<String, Hospedagem> hosp;
        hosp = new ConcurrentHashMap<>();
        
        Hospedagem h = new Hospedagem();
        h.setDestino("saopaulo");
        h.adicionarQuartosPorData(10, "2016-06-01");
        h.adicionarQuartosPorData(10, "2016-06-02");
        h.adicionarQuartosPorData(10, "2016-06-03");
        hosp.put(h.getId(), h);
        
        h = new Hospedagem();
        h.setDestino("catarina");
        h.adicionarQuartosPorData(20, "2016-06-01");
        h.adicionarQuartosPorData(20, "2016-06-02");
        h.adicionarQuartosPorData(20, "2016-06-03");
        hosp.put(h.getId(), h);
        
        return hosp;
    }
    
    public static ConcurrentMap<String, Voo> iniciarVoos() {
        ConcurrentMap<String, Voo> voos;
        voos = new ConcurrentHashMap<>();
        
        Voo v = new Voo();
        v.setOrigem("curitiba");
        v.setDestino("saopaulo");
        v.setData("2016-06-01");
        voos.put(v.getId(), v);
        
        v = new Voo();
        v.setOrigem("saopaulo");
        v.setDestino("curitiba");
        v.setData("2016-06-01");
        voos.put(v.getId(), v);
        
        v = new Voo();
        v.setOrigem("saopaulo");
        v.setDestino("curitiba");
        v.setData("2016-06-02");
        voos.put(v.getId(), v);
        
        v = new Voo();
        v.setOrigem("saopaulo");
        v.setDestino("curitiba");
        v.setData("2016-06-03");
        voos.put(v.getId(), v);
        
        return voos;
    }
}
