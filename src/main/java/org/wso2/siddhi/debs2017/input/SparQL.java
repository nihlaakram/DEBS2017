package org.wso2.siddhi.debs2017.input;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;



/*
* Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
public class SparQL {

    private static ArrayList<String> str = new ArrayList<String>();
    private static Float[] dataArr = new Float[120];
    private  static File file;
    private static FileWriter writer = null;
    private static final Logger log = Logger.getLogger(SparQL.class);




    public static void excuteQuery( File fileName)  {
        file = fileName;
        try {
            file.createNewFile();
            writer = new FileWriter(file);
            String data;
            Model model = RDFDataMgr.loadModel("molding_machine_100M_rdf.ttl");
            String queryString = " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                    "PREFIX wmm: <http://www.agtinternational.com/ontologies/WeidmullerMetadata#>" +
                    "PREFIX debs:<http://project-hobbit.eu/resources/debs2017#>" +
                    "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>" +
                    "PREFIX IoTCore: <http://www.agtinternational.com/ontologies/IoTCore#>" +
                    "PREFIX i40: <http://www.agtinternational.com/ontologies/I4.0#>" +
                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
                    "SELECT ?machine ?time ?timeStamp ?value ?property WHERE { " +
                    "?a rdf:type i40:MoldingMachineObservationGroup ." +
                    "?a i40:machine ?machine .  " +
                    "?a ssn:observationResultTime ?time ." +
                    "?time IoTCore:valueLiteral ?timeStamp ." +
                    "?a i40:contains ?b ." +
                    "?b ssn:observationResult ?c ." +
                    "?c ssn:hasValue ?d ." +
                    "?b ssn:observedProperty ?property ." +
                    "?d  IoTCore:valueLiteral ?value" +
                    "}" +
                    "ORDER BY ASC (?timeStamp)";
            Query query = QueryFactory.create(queryString);
            try {
                QueryExecution qexec = QueryExecutionFactory.create(query, model);
                ResultSet results = qexec.execSelect();
                results = ResultSetFactory.copyResults(results);

                //ResultSetFormatter.out(System.out, results, query) ;

                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    // RDFNode x = soln.get("time") ;       // Get a result variable by name.
                    Resource time = soln.getResource("time"); // Get a result variable - must be a resource
                    Resource property = soln.getResource("property");
                    Resource machine = soln.getResource("machine");
                    Literal value = soln.getLiteral("value");
                    //Literal timeStamp = soln.getLiteral("timeStamp");// Get a result variable - must be a literal

                    if (value.toString().contains("#string")) {
                            //do nothing
                    } else {

                        data = machine.getLocalName()+","+time.getLocalName()+","+property.getLocalName()+","+value.getFloat()+"\n";
                        writer.write(data);
                    }


                    // System.out.println(r);
                }
            } catch (Exception e) {
                log.info(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
