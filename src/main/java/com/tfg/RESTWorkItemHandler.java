package com.tfg;

import java.util.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkItemHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class RESTWorkItemHandler implements WorkItemHandler {

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String url = (String) workItem.getParameter("url"); // Obtener el parámetro "url"
        String metodo = (String) workItem.getParameter("metodo");//get o put
        String tipo = (String) workItem.getParameter("tipo");// medico o cliente
        
        switch(metodo){
        case "get":
        	peticionGET(workItem, manager, url, tipo);
        	break;
        case "put":
        	System.out.println("Petición put");;
        	//peticionPUT(workItem,manager,url,tipo);
        	break;
        }
        
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // No se necesita implementar en este caso
    }
    
    private void peticionGET(WorkItem workItem, WorkItemManager manager, String url, String tipo) {
        String urlPeticion="";
        System.out.println("Haciendo peticion GET");
        switch(tipo) {
        case "medico":
        	urlPeticion=url+"/Practitioner?_format=json";
        	break;
        case "paciente":
        	urlPeticion=url+"/Patient?_format=json";
        	break;
        }
        
        try {
            String jsonResponse = performHttpGet(urlPeticion);
            
            Map<String, Object> results = new HashMap<>();
            results.put("Result", jsonResponse.toString());
            System.out.println(jsonResponse.toString());
            manager.completeWorkItem(workItem.getId(), results);
        } catch (Exception e) {
            // Manejar errores
            manager.abortWorkItem(workItem.getId());
            e.printStackTrace();
        }
    }
    
    /*private void peticionPUT(WorkItem workItem, WorkItemManager manager, String url, String tipo) {
        String urlPeticion = url+"/Patient/131313jbm"; // Ajusta la URL
        try {
            String jsonResponse = performHttpPut(urlPeticion);
            
            Map<String, Object> results = new HashMap<>();
            results.put("Result", jsonResponse.toString());
            manager.completeWorkItem(workItem.getId(), results);
        } catch (Exception e) {
            // Manejar errores
            manager.abortWorkItem(workItem.getId());
            e.printStackTrace();
        }
    }*/
    
    private String performHttpGet(String url) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
        try {
            HttpGet httpGet = new HttpGet(url);
            
            CloseableHttpResponse response = httpClient.execute(httpGet);
            
            try {
                HttpEntity entity = response.getEntity();
                
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
        
        return null;
    }
    
   /* private String performHttpPut(String url) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
        	HttpPut httpPut = new HttpPut(url);
        	httpPut.setHeader("Content-Type", "application/fhir+json");
        	String jsonPayload = "{\"resourceType\":\"Patient\",\"id\":\"131313\",\"name\":[{\"given\":[\"juanma\"]}]}"; // JSON de actualización
        	
        	StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
            httpPut.setEntity(entity);
            
            CloseableHttpResponse response = httpClient.execute(httpPut);
            String responseBody = EntityUtils.toString(response.getEntity());
            return responseBody;
        }finally {
        	httpClient.close();
        }
    }*/
}
