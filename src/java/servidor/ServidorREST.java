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
    
    private static final ConcurrentMap<String, Voo> VOOS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Hospedagem> HOSPEDAGENS = new ConcurrentHashMap<>();
    
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
        
        System.out.println(origem+dataIda+destino+dataVolta+volta);
        
        if (origem.equals("") || destino.equals("")) {
            return rb.status(400).entity("{\"size\":\"0\"}").build();
        }
        
        for (Map.Entry pair : VOOS.entrySet()) {
            Voo v = (Voo) pair.getValue();
            
            // procura por voo ida
            if (destino.equals(v.getDestino()) && origem.equals(v.getOrigem())
                    && (dataIda.equals("") || dataIda.equals(v.getData()))) {
                list.add(v);
                continue;
            }
            // procurar por volta ou nao
            if (!volta.equals("1")) {
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
     * formato: <code>{"mensagem":Y}</code>, onde Y pode ser uma mensagem de 
     * 'erro' ou 'sucesso'.
     * @param ida Identificador do voo de ida
     * @param volta Identificador do voo de volta
     * @param assentos Número de assentos (ida e volta)
     * @return 
     */
    @POST
    @Produces("application/json")
    @Path("/comprarvoo")
    public Response comprarVoo(
            @DefaultValue("") @QueryParam("ida") String ida,
            @DefaultValue("") @QueryParam("volta") String volta,
            @DefaultValue("0") @QueryParam("assentos") String assentos) {
        
        ResponseBuilder rb = Response.status(200);
       
        System.out.println(ida+volta+assentos);
       
        // parametros incorretos
        if (ida.equals("") || assentos.equals("0")) {
            return rb.status(400).entity("{\"mensagem\":\"parâmetros incorretos\"}").build();
        }
        // voo ida não existe ou não tem assentos
        Integer intAssentos = Integer.parseInt(assentos);
        if (!VOOS.containsKey(ida) || VOOS.get(ida).getAssentos() < intAssentos) {
            return rb.status(404).entity("{\"mensagem\":\"voo não disponível\"}").build();
        }
        // voo volta está definido
        if (!volta.equals("")) {
             // voo volta não existe ou não tem assentos
             if (!VOOS.containsKey(volta) || VOOS.get(volta).getAssentos() < intAssentos) {
                 return rb.status(404).entity("{\"mensagem\":\"voo não disponível\"}").build();
             }
             VOOS.get(volta).diminuirAssentos(intAssentos);
        }
        VOOS.get(ida).diminuirAssentos(intAssentos);

        return rb.entity("{\"mensagem\":\"sucesso\"}").build();
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
        
        System.out.println(destino+dataEntrada+dataSaida);
        
        if (destino.equals("") || dataEntrada.compareTo(dataSaida) > 0) {
            return rb.status(400).entity("{\"size\":\"0\"}").build();
        }
        
        for (Map.Entry pair : HOSPEDAGENS.entrySet()) {
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
     * o seguinte formato: <code>{"mensagem":Y}</code>, onde Y pode ser 
     * uma mensagem 'erro' ou 'sucesso'.
     * @param destino Identificador da hospedagem
     * @param dataEntrada
     * @param dataSaida
     * @param quartos Número de quartos desejados
     * @return 
     */
    @POST
    @Produces("application/json")
    @Path("/comprarhospedagem")
    public synchronized Response comprarHospedagem(
            @DefaultValue("") @QueryParam("destino") String destino,
            @DefaultValue("") @QueryParam("dataentrada") String dataEntrada,
            @DefaultValue("") @QueryParam("datasaida") String dataSaida,
            @DefaultValue("0") @QueryParam("quartos") String quartos) {
        
        ResponseBuilder rb = Response.status(200);
        
        System.out.println(destino+dataEntrada+dataSaida+quartos);
        
        // parâmetros incorretos
        if (destino.equals("") || dataEntrada.equals("") 
                || dataSaida.equals("") || quartos.equals("0")
                || dataEntrada.compareTo(dataSaida) > 0) {
            return rb.status(400).entity("{\"mensagem\":\"Parâmetros incorretos\"}").build();
        }
        // destino não existe
        if (!HOSPEDAGENS.containsKey(destino)) {
            return rb.status(404).entity("{\"mensagem\":\"Hospedagem não existe\"}").build();
        }
        // veriricar disponibilidade de quartos por dia
        Integer intQuartos = Integer.parseInt(quartos);
        Hospedagem h = new Hospedagem(HOSPEDAGENS.get(destino));
        for (Map.Entry pair : h.getQuartosPorData().entrySet()) {
            String data = (String) pair.getKey();
            Integer numQuartos = (Integer) pair.getValue();
        
            // ignorar datas que não estão no intervalo esperado
            if (data.compareTo(dataEntrada) < 0 || data.compareTo(dataSaida) > 0) {
                continue;
            }
            // número de quartos disponíveis menor que desejados
            if (intQuartos > numQuartos) {
                return rb.status(404).entity("{\"mensagem\":\"Quartos não disponíveis\"}").build();
            }
            h.diminuirQuartosPorData(intQuartos, data);
        }
        HOSPEDAGENS.put(h.getId(), h);
        
        return rb.entity("{\"mensagem\":\"sucesso\"}").build();
    }
    
    @GET
    @Path("/iniciar")
    public Response iniciar() {
        Util.iniciarHospedagens(HOSPEDAGENS);
        Util.iniciarVoos(VOOS);

        return Response.status(200).build();
    }
}
