/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import modelo.Hospedagem;
import modelo.Voo;
import util.Util;

/**
 *
 * @author alexandre
 */
@Path("/serv")
public class ServidorREST {
    
    private final ConcurrentMap<String, Voo> voos;
    private final ConcurrentMap<String, Hospedagem> hospedagens;
    
    public ServidorREST() {
        this.hospedagens = new ConcurrentHashMap<>();
        this.voos = new ConcurrentHashMap<>();
        
        Voo v = new Voo();
        v.setOrigem("ctba");
        v.setDestino("sp");
        v.setAssentos(10);
        v.setData("2016-05-26");
        v.setPreco(new Float(10));
        voos.put(v.getId(), v);
        
        v = new Voo();
        v.setOrigem("sp");
        v.setDestino("ctba");
        v.setAssentos(10);
        v.setData("2016-05-30");
        v.setPreco(new Float(10));
        voos.put(v.getId(), v);
    }
    
    @GET
    @Produces("application/json")
    @Path("/consultarvoo")
    public Response consultarVoo(
            @DefaultValue("") @QueryParam("origem") String origem, 
            @DefaultValue("") @QueryParam("dataida") String dataIda, 
            @DefaultValue("") @QueryParam("destino") String destino, 
            @DefaultValue("") @QueryParam("datavolta") String dataVolta,
            @DefaultValue("0") @QueryParam("somenteida") String somenteIda) {
        
        Response.ResponseBuilder rb = Response.status(200);
        ArrayList<Voo> list = new ArrayList<>();
        
        /*System.out.println("acessou");
        long time = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < time) {}*/
        
        if (origem.equals("") || destino.equals("")) {
            return rb.status(400).entity("{\"size\":\"0\"}").build();
        }
        
        for (Map.Entry pair : voos.entrySet()) {
            Voo v = (Voo) pair.getValue();
            
            // procura por voo ida
            if (destino.equals(v.getDestino()) && origem.equals(v.getOrigem())
                    && (dataIda.equals("") || dataIda.equals(v.getData()))) {
                list.add(v);
                continue;
            }
            // procurar por volta ou nao
            if (somenteIda.equals("0")) {
                continue;
            }
            // procura por voo volta
            if (destino.equals(v.getOrigem()) && origem.equals(v.getDestino())
                    && (dataVolta.equals("") || dataVolta.equals(v.getData()))) {
                list.add(v);
            }
        }
                
        return rb.entity(Util.toJSON(list)).build();
    }
    
    /**
     * Recebe os ids dos voos de ida e volta e o total de assentos.
     * Se ida ou assento nao forem definidos retorna: 
     * <code>{status:0, mensagem:erro}</code>. 
     * Em sucesso retorna: <code>{status:1, mensagem:sucesso}</code>.
     * @param ida
     * @param volta
     * @param assento
     * @return 
     */
    @GET
    @POST
    @Produces("application/json")
    @Path("/comprarvoo")
    public Response comprarVoo(
            @DefaultValue("") @QueryParam("ida") String ida,
            @DefaultValue("") @QueryParam("volta") String volta,
            @DefaultValue("") @QueryParam("assento") String assento) {
        
       Response.ResponseBuilder rb = Response.status(200);
       
       // parametros incorretos
       if (ida.equals("") || assento.equals("") || assento.equals("0")) {
           return rb.status(400).entity("{\"status\":\"0\",\"mensagem\":\"erro\"}").build();
       }
       // comprar ida
       if (voos.containsKey(ida)) {
           voos.get(ida).diminuirAssentos(Integer.parseInt(assento));
       }
       // comprar volta
       if (!volta.equals("") && voos.containsKey(volta)) {
           voos.get(volta).diminuirAssentos(Integer.parseInt(assento));
       }
       rb.entity("{\"status\":\"1\",\"mensagem\":\"sucesso\"}");
       
       return rb.build();
    }
}
