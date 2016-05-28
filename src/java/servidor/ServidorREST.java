/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
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
        this.hospedagens = Util.iniciarHospedagens();
        this.voos = Util.iniciarVoos();
    }
    
    /**
     * Realiza uma consulta de voos disponíveis. 
     * URI: host/contexto/rest/serv/consultarvoo. 
     * A origem e o destino são obrigatórios. Se o parâmetro volta estiver 
     * com um valor diferente de 1, somente voos de ida são buscados. A resposta 
     * segue o seguinte formato: <code>{size:N, array:Objetos}</code>, onde 
     * Objetos são os voos que verificam com a busca e N o total de Objetos.
     * @param origem
     * @param dataIda
     * @param destino
     * @param dataVolta
     * @param volta
     * @return 
     */
    @GET
    @Produces("application/json")
    @Path("/consultarvoo")
    public Response consultarVoo(
            @DefaultValue("") @QueryParam("origem") String origem, 
            @DefaultValue("") @QueryParam("dataida") String dataIda, 
            @DefaultValue("") @QueryParam("destino") String destino, 
            @DefaultValue("") @QueryParam("datavolta") String dataVolta,
            @DefaultValue("1") @QueryParam("volta") String volta) {
        
        ResponseBuilder rb = Response.status(200);
        ArrayList<Voo> list = new ArrayList<>();
        
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
            if (volta.equals("0")) {
                continue;
            }
            // procura por voo volta
            if (destino.equals(v.getOrigem()) && origem.equals(v.getDestino())
                    && (dataVolta.equals("") || dataVolta.equals(v.getData()))) {
                list.add(v);
            }
        }
                
        return rb.entity(Util.toJSONVoos(list)).build();
    }
    
    /**
     * Compra voo de ida e volta baseado no número de assentos. 
     * URI: host/contexto/rest/serv/comprarvoo. 
     * Ida e assentos são obrigatórios. Verifica se o número de assentos está 
     * disponível, antes de concluir a compra. A resposta segue o seguinte 
     * formato: <code>{status:X, mensagem:Y}</code>, onde X pode ser 0 ou 1, 
     * acompanhado da mensagem 'erro' ou 'sucesso', respectivamente.
     * @param ida Identificador do voo de ida
     * @param volta Identificador do voo de volta
     * @param assentos Número de assentos (ida e volta)
     * @return 
     */
    @GET
    @POST
    @Produces("application/json")
    @Path("/comprarvoo")
    public Response comprarVoo(
            @DefaultValue("") @QueryParam("ida") String ida,
            @DefaultValue("") @QueryParam("volta") String volta,
            @DefaultValue("") @QueryParam("assentos") String assentos) {
        
       ResponseBuilder rb = Response.status(200);
       
       // parametros incorretos
       if (ida.equals("") || assentos.equals("") || assentos.equals("0")) {
           return rb.status(400).entity("{\"status\":\"0\",\"mensagem\":\"erro\"}").build();
       }
       // comprar ida
       if (voos.containsKey(ida)) {
           voos.get(ida).diminuirAssentos(Integer.parseInt(assentos));
       }
       // comprar volta
       if (!volta.equals("") && voos.containsKey(volta)) {
           voos.get(volta).diminuirAssentos(Integer.parseInt(assentos));
       }
       
       return rb.entity("{\"status\":\"1\",\"mensagem\":\"sucesso\"}").build();
    }
    
    /**
     * Realiza uma consulta de hospedagens disponíveis. 
     * URI: host/contexto/rest/serv/consultarhospedagem. 
     * O parâmetro destino é obrigatório. A data de entrada deve sermpre vir 
     * acompanhada da data de saída e vice-versa. A resposta segue o seguinte 
     * formato: <code>{size:N, array:Objetos}</code>, onde Objetos são os voos 
     * que verificam com a busca e N o total de Objetos.
     * @param destino
     * @param dataEntrada
     * @param dataSaida
     * @return 
     */
    @GET
    @Produces("application/json")
    @Path("/consultarhospedagem")
    public Response consultarHospedagem(
            @DefaultValue("") @QueryParam("destino") String destino, 
            @DefaultValue("") @QueryParam("dataentrada") String dataEntrada, 
            @DefaultValue("") @QueryParam("datasaida") String dataSaida) {
        
        ResponseBuilder rb = Response.status(200);
        ArrayList<Hospedagem> list = new ArrayList<>();
        
        if (destino.equals("")) {
            return rb.status(400).entity("{\"size\":\"0\"}").build();
        }
        
        for (Map.Entry pair : hospedagens.entrySet()) {
            Hospedagem h = (Hospedagem) pair.getValue();
            
            // nao eh o destino procurado
            if (!destino.equals(h.getDestino())) {
                continue;
            }
            // destino encontrado, mas sem datas definidas
            if (dataEntrada.equals("") && dataSaida.equals("")) {
                list.add(h);
                continue;
            }
            // destino encontrado com datas definidas
            if (h.getQuartosPorData().containsKey(dataEntrada) 
                    && h.getQuartosPorData().containsKey(dataSaida)) {
                list.add(h);
            }
        }
        
        return rb.entity(Util.toJSONHospedagens(list)).build();
    }
    
    /**
     * Compra hospedagem. Um período entre entrada e sáida.  
     * URI: host/contexto/rest/serv/comprarhospedagem. 
     * Destino, entrada, saida e quartos são obrigatórios. Verifica se o número 
     * de quartos está disponível, antes de concluir a compra. A resposta segue 
     * o seguinte formato: <code>{status:X, mensagem:Y}</code>, onde X pode ser 
     * 0 ou 1, acompanhado da mensagem 'erro' ou 'sucesso', respectivamente.
     * @param destino Identificador da hospedagem
     * @param dataEntrada
     * @param dataSaida
     * @param quartos Número de quartos desejados
     * @return 
     */
    @GET
    @POST
    @Produces("application/json")
    @Path("/comprarhospedagem")
    public synchronized Response comprarHospedagem(
            @DefaultValue("") @QueryParam("destino") String destino,
            @DefaultValue("") @QueryParam("dataentrada") String dataEntrada,
            @DefaultValue("") @QueryParam("datasaida") String dataSaida,
            @DefaultValue("0") @QueryParam("quartos") String quartos) {
        
        ResponseBuilder rb = Response.status(200);
        
        // parâmetros incorretos
        if (destino.equals("") || dataEntrada.equals("") 
                || dataSaida.equals("") || quartos.equals("0")) {
            return rb.status(400).entity("{\"status\":\"0\",\"mensagem\":\"erro\"}").build();
        }
        // destino não existe
        if (!hospedagens.containsKey(destino)) {
            return rb.status(400).entity("{\"status\":\"0\",\"mensagem\":\"erro\"}").build();
        }
        // veriricar disponibilidade de quartos por dia
        Integer intQuartos = Integer.parseInt(quartos);
        Hospedagem h = hospedagens.get(destino);
        for (Map.Entry pair : h.getQuartosPorData().entrySet()) {
            String data = (String) pair.getKey();
            Integer numQuartos = (Integer) pair.getValue();
        
            // ignorar datas que não estão no intervalo esperado
            if (data.compareTo(dataEntrada) < 0 || data.compareTo(dataSaida) > 0) {
                continue;
            }
            // número de quartos disponíveis menor que desejados
            if (intQuartos > numQuartos) {
                return rb.status(400).entity("{\"status\":\"0\",\"mensagem\":\"erro\"}").build();
            }
            h.diminuirQuartosPorData(intQuartos, data);
        }
        hospedagens.put(h.getId(), h);
        
        return rb.entity("{\"status\":\"1\",\"mensagem\":\"sucesso\"}").build();
    }
}
