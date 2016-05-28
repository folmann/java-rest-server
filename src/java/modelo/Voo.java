package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

public class Voo implements Serializable {
    
    private String id;
    private String origem;
    private String destino;
    private String data;
    private Integer assentos;
    private Float preco;
    
    public Voo() {
        // id gerado automaticamente
        id = Integer.toString(
                (new Long((new Date().getTime())/1000).intValue()) 
                    + (new Random().nextInt(10000)));
        data = LocalDate.now().toString();
        assentos = 10;
        preco = (float) 10;
    }
    
    public void diminuirAssentos(Integer assentos) {
        if (this.assentos >= assentos) {
            this.assentos -= assentos;
        }
    }
    
    // getters e setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getOrigem() {
        return origem;
    }
    public void setOrigem(String origem) {
        this.origem = origem;
    }
    public String getDestino() {
        return destino;
    }
    public void setDestino(String destino) {
        this.destino = destino;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public Integer getAssentos() {
        return assentos;
    }
    public void setAssentos(Integer assentos) {
        this.assentos = assentos;
    }
    public Float getPreco() {
        return preco;
    }
    public void setPreco(Float preco) {
        this.preco = preco;
    }   
}
