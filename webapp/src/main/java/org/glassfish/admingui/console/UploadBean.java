/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glassfish.admingui.console;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.sun.faces.taglib.jsf_core.SelectItemTag;
import org.apache.myfaces.trinidad.model.UploadedFile;
import org.glassfish.admingui.console.event.DragDropEvent;
import org.glassfish.admingui.console.rest.JSONUtil;
import org.glassfish.admingui.console.rest.RestUtil;
import org.glassfish.admingui.console.util.CommandUtil;
import org.glassfish.admingui.console.util.FileUtil;
import org.glassfish.admingui.console.util.GuiUtil;
import org.glassfish.admingui.console.util.TargetUtil;


/**
 *
 * @author anilam
 */
@ManagedBean
@ViewScoped
public class UploadBean {
    private UploadedFile _file;
    private File tmpFile;
    private String appName;
    private String desc;
    private String contextRoot;
    private List<Map<String, Object>> metaData;

    private String database;
    private String eeTemplate;
    private String loadBalancer;

    private List<String> eeTemplates = new ArrayList<String>() {{
        add("GlassFish Small");
        add("GlassFish Medium");
        add("GlassFish Large");
    }};

    private List<String> databases = new ArrayList<String>() {{
        add("Derby");
        add("MySQL");
        add("Oracle");
    }};

    private List<String> loadBalancers = new ArrayList<String>() {{
        add("foo");
        add("bar");
        add("baz");
    }};

    private List<SelectItem> databaseSelectItems;
    private List<SelectItem> eeTemplateSelectItems;
    private List<SelectItem> loadBalancerSelectItems;

    private List<Map> databasesMetaData = new ArrayList();
    private List<Map> eeTemplatesMetaData = new ArrayList();
    private List<Map> loadBalancersMetaData = new ArrayList();

    /*{
        //metaData = CommandUtil.getPreSelectedServices("D:/Projects/console.next/svn/main/appserver/tests/paas/basic-db/target/basic_db_paas_sample.war");
        metaData = CommandUtil.getPreSelectedServices("/opt/console.next/svn/main/appserver/tests/paas/basic-db/target/basic_db_paas_sample.war");
        processMetaData();
    }*/

    void createSelectItems(String serviceType) {
        List<SelectItem> selectItems = new ArrayList();
        List<String> templateList = CommandUtil.getTemplateList(serviceType);
        for (String template : templateList) {
            selectItems.add(new SelectItem(template));
        }
        if (CommandUtil.SERVICE_TYPE_RDMBS.equals(serviceType)) {
            databaseSelectItems =  selectItems;
            databases = templateList;
        } else if (CommandUtil.SERVICE_TYPE_JAVAEE.equals(serviceType)) {
            eeTemplateSelectItems =  selectItems;
            eeTemplates = templateList;
        } else if (CommandUtil.SERVICE_TYPE_LB.equals(serviceType)) {
            loadBalancerSelectItems =  selectItems;
            loadBalancers = templateList;
        }
    }

    void processMetaData() {
        for(Map oneService : metaData){
            String serviceType = (String) oneService.get("service-type");
            if (CommandUtil.SERVICE_TYPE_RDMBS.equals(serviceType)) {
                databasesMetaData.add(oneService);
            } else if (CommandUtil.SERVICE_TYPE_JAVAEE.equals(serviceType)) {
                eeTemplatesMetaData.add(oneService);
            } else if (CommandUtil.SERVICE_TYPE_LB.equals(serviceType)) {
                loadBalancersMetaData.add(oneService);
            }
        }

        if (databasesMetaData != null) {
            createSelectItems(CommandUtil.SERVICE_TYPE_RDMBS);
        }
        if (eeTemplatesMetaData != null) {
            createSelectItems(CommandUtil.SERVICE_TYPE_JAVAEE);
        }
        if (loadBalancersMetaData != null) {
            createSelectItems(CommandUtil.SERVICE_TYPE_LB);
        }
    }

    public void fileUploaded(ValueChangeEvent event) {
        System.out.println("------ in filUploaded");
        UploadedFile file = (UploadedFile) event.getNewValue();
        try{
            if (file != null) {
                //FacesContext context = FacesContext.getCurrentInstance();
                //FacesMessage message = new FacesMessage( "Successfully uploaded file " + file.getFilename() + " (" + file.getLength() + " bytes)");
                //context.addMessage(event.getComponent().getClientId(context), message);
                // Here's where we could call file.getInputStream()
                System.out.println("getFilename=" + file.getFilename());
                System.out.println("getLength=" + file.getLength());
                System.out.println("getContentType=" + file.getContentType());
                tmpFile = FileUtil.inputStreamToFile(file.getInputStream(), file.getFilename());

                //each Map is a Service that will be provisioned
                metaData =  CommandUtil.getPreSelectedServices(tmpFile.getAbsolutePath());
                processMetaData();

                System.out.println("metaData = " + metaData);
                Map dpAttrs = new HashMap();
                dpAttrs.put("archive" , tmpFile.getAbsolutePath());
                dpAttrs.put("test", "test String");
                dpAttrs.put("modifiedServiceDesc", metaData);
                Map res = (Map) RestUtil.restRequest(REST_URL + "/applications/_generate-glassfish-services-deployment-plan", dpAttrs, "GET", null, null, false, true).get("data");
                System.out.println(res);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public UploadedFile getFile() {
        return _file;
    }

    public void setFile(UploadedFile file) {
        _file = file;
    }

    public String doDeploy(){


        // Generate deployment plan based on modified service
        Map dpAttrs = new HashMap();
        dpAttrs.put("archive" , tmpFile.getAbsolutePath());
        String metaDataJson = JSONUtil.javaToJSON(metaData, -1);
        dpAttrs.put("modifiedServiceDesc", metaDataJson);
        //ensure that template-id is the same as templateId, ie whatever user has changed that to.
        Map res = (Map) RestUtil.restRequest(REST_URL + "/applications/_generate-glassfish-services-deployment-plan", dpAttrs, "POST", null, null, false, true).get("data");
        Map extr = (Map) res.get("extraProperties");
        String deploymentPlanPath = (String) extr.get("deployment-plan-file-path");
        System.out.println( extr.get("deployment-plan-file-path"));

        Map payload = new HashMap();
        //uncomment out when backend can generate the deployment plan.
        //payload.put("deploymentplan", deploymentPlanPath);

        payload.put("id", this.tmpFile.getAbsolutePath());
        if (!GuiUtil.isEmpty(this.appName)){
            payload.put("name", this.appName);
        }
        if (!GuiUtil.isEmpty(this.contextRoot)){
            payload.put("contextroot", this.contextRoot);
        }
        /*
        if (!GuiUtil.isEmpty(this.desc)){
            payload.put("name", this.desc);
        }
         *
         */
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        List deployingApps = (List)sessionMap.get("_deployingApps");
        try {
            if (deployingApps == null ){
                deployingApps = new ArrayList();
                sessionMap.put("_deployingApps", deployingApps);
            }
            deployingApps.add(this.appName);
            RestUtil.restRequest(REST_URL + "/applications/application", payload, "post", null, null, false, true);
            return "/demo/applications";
        } catch (Exception ex) {
            if (deployingApps != null && deployingApps.contains(this.appName)){
                deployingApps.remove(this.appName);
            }
            ex.printStackTrace();
            System.out.println("------------- do Deploy returns NULL");
            return null;
        }
    }

    public String getAppName(){
        return appName;
    }
    public void setAppName(String nm){
        this.appName = nm;
    }

    public String getDescription(){
        return desc;
    }
    public void setDescription(String description){
        this.desc = description;
    }

    public String getContextRoot(){
        return contextRoot;
    }
    public void setContextRoot(String ctxRoot){
        this.contextRoot = ctxRoot;
    }


    public List getMetaData(){
        return metaData;
    }
    public void setMetaData(List nm){
        this.metaData = nm;
    }

    public String databaseDropListener(DragDropEvent event) {
        String value = (String) event.getData();
        if (database != null) {
            databases.add(database);
        }
        database = value;
        databases.remove(database);
        Collections.sort(databases);

        return null;
    }

    public String loadBalancerDropListener(DragDropEvent event) {
        String value = (String) event.getData();
        if (loadBalancer != null) {
            loadBalancers.add(loadBalancer);
        }
        loadBalancer = value;
        loadBalancers.remove(loadBalancer);
        Collections.sort(loadBalancers);

        return null;
    }

    public String eeTemplateDropListener(DragDropEvent event) {
        String value = (String) event.getData();
        if (eeTemplate != null) {
            eeTemplates.add(eeTemplate);
        }
        eeTemplate = value;
        eeTemplates.remove(eeTemplate);
        Collections.sort(eeTemplates);

        return null;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getEeTemplate() {
        return eeTemplate;
    }

    public void setEeTemplate(String eeTemplate) {
        this.eeTemplate = eeTemplate;
    }

    public String getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(String loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public List<SelectItem> getDatabaseSelectItems() {
        return databaseSelectItems;
    }

    public List<Map> getDatabasesMetaData() {
        return databasesMetaData;
    }

    public List<SelectItem> getEeTemplateSelectItems() {
        return eeTemplateSelectItems;
    }

    public List<Map> getEeTemplatesMetaData() {
        return eeTemplatesMetaData;
    }

    public String getEeTemplateMinMaxInstances() {
        /*
        Map config = (Map)eeTemplateMetaData.get("configurations");
        String min = (String) config.get("min.clustersize");
        String max = (String) config.get("max.clustersize");
        return min + " - " + max;
        */
        return "";
    }

    public void setEeTemplateMinMaxInstances(String minMaxInstances) {
        /*
        String minmax[] = minMaxInstances.split("-");
        Map config = (Map)eeTemplateMetaData.get("configurations");
        config.put("min.clustersize", minmax[0].trim());
        config.put("max.clustersize", minmax[1].trim());
        */
    }

    public List<SelectItem> getLoadBalancerSelectItems() {
        return loadBalancerSelectItems;
    }

    public List<Map> getLoadBalancersMetaData() {
        return loadBalancersMetaData;
    }

    static final String REST_URL="http://localhost:4848/management/domain";
}
