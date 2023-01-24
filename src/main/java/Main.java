import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        System.out.println(json);
        String jsonFile = "data.json";
        String jsonFile2 = "data2.json";
        String xmlFile = "data.xml";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        List<Employee> list2 = getListEmployeers(doc);
        String jsonString = listToJson(list2);
        System.out.println(jsonString);
        writeString(json, jsonFile);
        writeString(jsonString, jsonFile2);

    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
            staff.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return staff;
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }

    private static void writeString(String input, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(input);
            writer.flush();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static List<Employee> getListEmployeers(Document doc) {
        NodeList nodeList = doc.getElementsByTagName("employee");
        List<Employee> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                list.add(new Employee(
                        (Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent())),
                        (element.getElementsByTagName("firstName").item(0).getTextContent()),
                        (element.getElementsByTagName("lastName").item(0).getTextContent()),
                        (element.getElementsByTagName("country").item(0).getTextContent()),
                        (Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent()))));
            }
        }
        return list;
    }

}
