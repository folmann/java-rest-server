package modelo;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Hospedagem {
    
    private String id;
    private String destino;
    private Float preco;
    private final HashMap<String, Integer> quartosPorData;
    
    public Hospedagem() {
        id = Integer.toString(
                (new Long((new Date().getTime())/1000).intValue())
                    + (new Random().nextInt(10000)));
        preco = (float) 10;
        quartosPorData = new HashMap<>();
    }
    
    public Hospedagem(Hospedagem h) {
        id = h.getId();
        destino = h.getDestino();
        preco = h.getPreco();
        quartosPorData = new HashMap<>(h.getQuartosPorData());
    }
    
    public void adicionarQuartosPorData(Integer quartos, String data) {
        if (quartosPorData.containsKey(data)) {
            quartos += quartosPorData.get(data);
        }
        quartosPorData.put(data, quartos);
    }
    
    public void diminuirQuartosPorData(Integer quartos, String data) {
        if (!quartosPorData.containsKey(data)) {
            return;
        }
        if (quartosPorData.get(data) < quartos) {
            return;
        }
        adicionarQuartosPorData(-quartos, data);
    }
    
    // getters e setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDestino() {
        return destino;
    }
    public void setDestino(String destino) {
        this.destino = destino;
    }
    public Float getPreco() {
        return preco;
    }
    public void setPreco(Float preco) {
        this.preco = preco;
    }
    public HashMap<String, Integer> getQuartosPorData() {
        return quartosPorData;
    }
}