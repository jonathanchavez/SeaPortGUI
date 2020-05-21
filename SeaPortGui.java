//File: SeaPortGui.java
//Purpose: Project 2
//Date: 9/15/2019
//Author: Jonathan Chavez

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.String;
import java.util.*;


public class SeaPortGui {
    private JButton button1;
    private JPanel panelMain;
    private JTextArea displayText;
    private JButton searchButton;
    private JComboBox comboBox1;
    private JTextField searchField;
    private JButton sortButton;
    private JComboBox comboBox2;
    private JScrollPane Scroll;
    private JPanel progressPanel;
    private JTree portTree;
    private JTextArea resourceText;
    private JButton updateButton;

    public File file;
    Scanner scan;
    World world = new World();

    HashMap<Integer, SeaPort> portList;
    HashMap<Integer, Ship> shipList;


    public SeaPortGui() {
        HashMap<Integer, SeaPort> seaPortHashMap = new HashMap<>();
        HashMap<Integer, Ship> shipHashMap = new HashMap<>();
        HashMap<Integer, Dock> dockHashMap = new HashMap<>();
        HashMap<Integer, Person> personHashMap = new HashMap<>();
        HashMap<Integer, Job> jobHashMap = new HashMap<>();
        portList = seaPortHashMap;
        shipList = shipHashMap;

        //Deletes the pre made jtree before application runs
        DefaultTreeModel model = (DefaultTreeModel) portTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        model.reload();
        model.setRoot(null);

        //hash map to record all of the nodes paths to the root
        HashMap<Integer, TreePath> paths = new HashMap<>();


        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                //Sets up file chooser

                JFileChooser fc = new JFileChooser(".");
                fc.setDialogTitle("File Choser");

                //gets the file
                int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = fc.getSelectedFile();
                }


                try {
                    Scanner scan = new Scanner(file);
                    int nextLineCounter = 0;

                    //makes a new root for jtree
                    DefaultMutableTreeNode seaRoot = new DefaultMutableTreeNode("SeaPort");
                    model.setRoot(seaRoot);
                    world = new World();

                    //clears all maps and panels, for a new set of data
                    seaPortHashMap.clear();
                    shipHashMap.clear();
                    dockHashMap.clear();
                    personHashMap.clear();
                    jobHashMap.clear();
                    paths.clear();
                    progressPanel.removeAll();


                    //scan each first word of line and then sorts it by keyword
                    while (scan.hasNextLine()) {
                        switch (scan.next()) {
                            case "port":
                                String seaportname = scan.next();
                                int seaportindex = Integer.parseInt(scan.next());

                                //adds in new port
                                SeaPort seatemp = new SeaPort(seaportname, seaportindex, Integer.parseInt(scan.next()));

                                world.ports.add(seatemp);
                                //adds it to the hashmap
                                seaPortHashMap.put(seaportindex, seatemp);

                                DefaultMutableTreeNode ports = new DefaultMutableTreeNode(seatemp);
                                model.insertNodeInto(ports, seaRoot, seaRoot.getChildCount());

                                TreeNode[] portPath = ports.getPath();
                                TreePath pa = new TreePath(portPath);
                                paths.put(seaportindex, pa);

                                break;
                            case "dock":
                                String dockName = scan.next();
                                int dockIndex = Integer.parseInt(scan.next());
                                int dockParent = Integer.parseInt(scan.next());

                                int location = 0;
                                int dchecker = 0;

                                //checks to see which port the dock belongs to
                                for (int counter = 0; counter < world.ports.size(); counter++) {
                                    if (world.ports.get(counter).getIndex() == dockParent) {
                                        location = counter;
                                        dchecker++;
                                    }


                                }
                                if (dchecker == 0) {
                                    System.out.println("Error" + dockName);
                                }

                                Dock dockTemp = new Dock(dockName, dockIndex, dockParent, Integer.parseInt(scan.next()));

                                dockTemp.setPort(world.ports.get(location));

                                //adds in new dock to arraylist of the parent port
                                world.ports.get(location).docks.add(dockTemp);

                                //adds it to the dock hashmap
                                dockHashMap.put(dockIndex, dockTemp);


                                //makes a new node
                                DefaultMutableTreeNode dockNode = new DefaultMutableTreeNode(dockTemp);

                                //gets the path of the parent
                                TreePath portPa = paths.get(dockParent);

                                //adds the node to the parent
                                DefaultMutableTreeNode parentPath = (DefaultMutableTreeNode) portPa.getLastPathComponent();
                                model.insertNodeInto(dockNode, parentPath, parentPath.getChildCount());

                                //adds the nodes path to the map with all of the nodes path in it
                                TreeNode[] dockPath = dockNode.getPath();
                                TreePath doc = new TreePath(dockPath);
                                paths.put(dockIndex, doc);
                                model.reload();

                                break;
                            case "pship":
                                String name = scan.next();
                                int index = Integer.parseInt(scan.next());
                                int parent = Integer.parseInt(scan.next());
                                int plocation = 0;
                                int result = 0;
                                int dockResult = 0;
                                int pchecker = 0;
                                int pDChecker = 0;

                                //searches to see if parent is in port
                                if (parent >= 10000 && parent <= 19999) {


                                    for (int counter = 0; counter < world.ports.size(); counter++) {
                                        if (world.ports.get(counter).getIndex() == parent) {
                                            result = counter;
                                            pchecker++;
                                        }

                                    }
                                    if (pchecker == 0) {
                                        System.out.println("Error" + name);
                                    }
                                }
                                //searches to see if parent is in dock
                                else if (parent >= 20000 && parent <= 29999) {

                                    for (int counter = 0; counter < world.ports.size(); counter++) {
                                        for (int counter2 = 0; counter2 < world.ports.get(counter).docks.size(); counter2++) {
                                            if (world.ports.get(counter).docks.get(counter2).getIndex() == parent) {
                                                result = counter;
                                                dockResult = counter2;
                                                pDChecker++;
                                            }
                                        }
                                    }

                                    if (pDChecker == 0) {
                                        System.out.println("Error" + name);
                                    }


                                }

                                //adds in new passenger ship
                                PassengerShip temp = new PassengerShip(name, index, parent, Double.parseDouble(scan.next()),
                                        Double.parseDouble(scan.next()), Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                                        Integer.parseInt(scan.next()), Integer.parseInt(scan.next()), Integer.parseInt(scan.next()));
                                world.ports.get(result).ships.add(temp);
                                if (pDChecker != 0) {
                                    //if parent was dock then adds into dock class
                                    world.ports.get(result).docks.get(dockResult).setShip(temp);
                                } else {
                                    //else adds it to the que
                                    world.ports.get(result).que.add(temp);
                                }

                                //adds it into the ship hash map
                                shipHashMap.put(index, temp);

                                //makes new node, adds to parent node
                                DefaultMutableTreeNode shipNode = new DefaultMutableTreeNode(temp);
                                TreePath shipPa = paths.get(parent);
                                DefaultMutableTreeNode shipParentPath = (DefaultMutableTreeNode) shipPa.getLastPathComponent();
                                model.insertNodeInto(shipNode, shipParentPath, shipParentPath.getChildCount());

                                //record this nodes path
                                TreeNode[] shipPath = shipNode.getPath();
                                TreePath shi = new TreePath(shipPath);
                                paths.put(index, shi);


                                break;
                            case "cship":

                                String cname = scan.next();
                                int cindex = Integer.parseInt(scan.next());
                                int cparent = Integer.parseInt(scan.next());
                                int clocation = 0;
                                int cresult = 0;
                                int cDockResult = 0;
                                int cchecker = 0;
                                int cDChecker = 0;

                                //searches to see if parent is in port
                                if (cparent >= 10000 && cparent <= 19999) {


                                    for (int counter = 0; counter < world.ports.size(); counter++) {
                                        if (world.ports.get(counter).getIndex() == cparent) {
                                            cresult = counter;
                                            cchecker++;
                                        }


                                    }
                                    if (cchecker == 0) {
                                        System.out.println("Error" + cname);
                                    }
                                }

                                //searches to see if parent is in dock
                                else if (cparent >= 20000 && cparent <= 29999) {
                                    for (int counter = 0; counter < world.ports.size(); counter++) {
                                        for (int counter2 = 0; counter2 < world.ports.get(counter).docks.size(); counter2++) {
                                            if (world.ports.get(counter).docks.get(counter2).getIndex() == cparent) {
                                                cresult = counter;
                                                cDockResult = counter2;
                                                cDChecker++;
                                            }
                                        }
                                    }
                                    if (cDChecker == 0) {
                                        System.out.println("Error" + cname);
                                    }


                                }

                                //adds in new cargo ship
                                CargoShip ctemp = new CargoShip(cname, cindex, cparent, Double.parseDouble(scan.next()),
                                        Double.parseDouble(scan.next()), Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                                        Double.parseDouble(scan.next()), Double.parseDouble(scan.next()), Double.parseDouble(scan.next()));
                                world.ports.get(cresult).ships.add(ctemp);

                                if (cDChecker != 0) {
                                    //if parent was in dock then adds cargoship to dock class
                                    world.ports.get(cresult).docks.get(cDockResult).setShip(ctemp);
                                } else {
                                    //adds into que
                                    world.ports.get(cresult).que.add(ctemp);
                                }

                                //adds it into the ship hashmap
                                shipHashMap.put(cindex, ctemp);


                                //makes new node, adds it to parent node
                                DefaultMutableTreeNode cargoNode = new DefaultMutableTreeNode(ctemp);
                                TreePath cargoPa = paths.get(cparent);
                                DefaultMutableTreeNode cargoParentPath = (DefaultMutableTreeNode) cargoPa.getLastPathComponent();
                                model.insertNodeInto(cargoNode, cargoParentPath, cargoParentPath.getChildCount());

                                //records the nodes path
                                TreeNode[] cargoPath = cargoNode.getPath();
                                TreePath car = new TreePath(cargoPath);
                                paths.put(cindex, car);


                                break;

                            case "person":
                                String perName = scan.next();
                                int perIndex = Integer.parseInt(scan.next());
                                int perPar = Integer.parseInt(scan.next());
                                int perChecker = 0;
                                int perResult = 0;

                                //finds which port is its parent
                                for (int counter = 0; counter < world.ports.size(); counter++) {
                                    if (world.ports.get(counter).getIndex() == perPar) {
                                        perResult = counter;
                                        perChecker++;
                                    }

                                }
                                if (perChecker == 0) {
                                    System.out.println("Error" + perName);
                                }

                                Person personTemp = new Person(perName, perIndex, perPar, scan.next());

                                //adds in to parent port arraylist
                                world.ports.get(perResult).persons.add(personTemp);

                                //adds it into the person hashmap
                                personHashMap.put(perIndex, personTemp);

                                //makes new node, adds to parent
                                DefaultMutableTreeNode personNode = new DefaultMutableTreeNode(personTemp);
                                TreePath personPa = paths.get(perPar);
                                DefaultMutableTreeNode personParentPath = (DefaultMutableTreeNode) personPa.getLastPathComponent();
                                model.insertNodeInto(personNode, personParentPath, personParentPath.getChildCount());

                                //records nodes path
                                TreeNode[] personPath = personNode.getPath();
                                TreePath per = new TreePath(personPath);
                                paths.put(perIndex, per);


                                break;

                            case "job":

                                //gets the line of the job to new scanner
                                String line = scan.nextLine();
                                Scanner LineScanner = new Scanner(line);
                                nextLineCounter = 1;
                                //scans the line into its parts
                                String jobName = LineScanner.next();
                                int jobIndex = Integer.parseInt(LineScanner.next());
                                int jobParent = Integer.parseInt(LineScanner.next());
                                double jobDur = Double.parseDouble(LineScanner.next());
                                ArrayList<String> skills = new ArrayList<>();

                                //sees if it has more skills
                                while (LineScanner.hasNext()) {
                                    skills.add(LineScanner.next());
                                }

                                Job jobTemp = new Job(jobName, jobIndex, jobParent, jobDur, skills, progressPanel);

                                //adds it to ship
                                if (shipHashMap.containsKey(jobParent)) {
                                    shipHashMap.get(jobParent).addJob(jobTemp);
                                }


                                jobHashMap.put(jobIndex, jobTemp);
                                LineScanner.close();


                                //Adds job to the jtree puts it in the node of its parent
                                DefaultMutableTreeNode jobNode = new DefaultMutableTreeNode(jobTemp);
                                TreePath jobPa = paths.get(jobParent);
                                DefaultMutableTreeNode jobParentPath = (DefaultMutableTreeNode) jobPa.getLastPathComponent();
                                model.insertNodeInto(jobNode, jobParentPath, jobParentPath.getChildCount());

                                //record nodes path
                                TreeNode[] jobPath = jobNode.getPath();
                                TreePath jo = new TreePath(jobPath);
                                paths.put(jobIndex, jo);

                                break;


                        }
                        if (scan.hasNextLine() && nextLineCounter == 0) {
                            scan.nextLine();
                        }
                    }

                    scan.close();

                    //displays the data into the gui
                    //sends through the hashmaps
                    displayText.setText(textHashPrint(dockHashMap, shipHashMap, seaPortHashMap, personHashMap, jobHashMap));

                    //starts the threads of the jobs in the ships that are at the dock
                    for (Dock doc : dockHashMap.values()) {
                        doc.ship.dock(doc);
                    }

                    displayResources();


                } catch (FileNotFoundException | InterruptedException ex) {
                    ex.printStackTrace();
                }

            }
        });


        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                StringBuilder result = new StringBuilder();
                String search = searchField.getText();


                result.append("Search List: \n\n");

                //checks to see if user selected name as search field
                if (comboBox1.getSelectedIndex() == 0) {

                    //looks through all of the hashmaps to see if the name is in thier hashmap
                    for (SeaPort seas : seaPortHashMap.values()) {
                        if (seas.getName().equals(search)) {
                            result.append(seas.toString());
                            result.append("\n");
                        }
                    }

                    for (Dock docs : dockHashMap.values()) {
                        if (docs.getName().equals(search)) {
                            result.append(docs.toString());
                            result.append("\n");
                        }
                    }

                    for (Ship ships : shipHashMap.values()) {
                        if (ships.getName().equals(search)) {
                            result.append(ships.toString());
                            result.append("\n");
                        }
                    }

                    for (Person persons : personHashMap.values()) {
                        if (persons.getName().equals(search)) {
                            result.append(persons.toString());
                            result.append("\n");
                        }
                    }

                }

                //checks to see if user selected index
                else if (comboBox1.getSelectedIndex() == 1) {
                    int searchNum = Integer.parseInt(searchField.getText());

                    //checks to see if the index in in the ports hashmap
                    if (seaPortHashMap.containsKey(searchNum)) {
                        result.append(seaPortHashMap.get(searchNum).toString());
                        result.append("\n");
                    }


                    //checks to see if the index is in the dock hashmap
                    if (dockHashMap.containsKey(searchNum)) {
                        result.append(dockHashMap.get(searchNum).toString());
                        result.append("\n");
                    }

                    //checks to see if the index is in the ship hashmap
                    if (shipHashMap.containsKey(searchNum)) {
                        result.append(shipHashMap.get(searchNum).toString());
                        result.append("\n");
                    }

                    //checks to see if the index is in the person hashmap
                    if (personHashMap.containsKey(searchNum)) {
                        result.append(personHashMap.get(searchNum).toString());
                        result.append("\n");
                    }


                }

                //checks to see if user selected skil
                else if (comboBox1.getSelectedIndex() == 2) {

                    //goes through person hashmap to see if it has the skill
                    for (Person skills : personHashMap.values()) {
                        if (skills.skill.equals(search)) {
                            result.append(skills.toString());
                            result.append("\n");
                        }
                    }


                }

                //displays the results of the search
                displayText.setText(result.toString());


            }
        });
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //sort the data structure

                //calls the classes sort function
                for (SeaPort seas : seaPortHashMap.values()) {

                    seas.compare(comboBox2.getSelectedIndex());

                }
                world.Sort();
                String choice = " ";

                if (comboBox2.getSelectedIndex() == 0) {
                    choice = "Weight";
                } else if (comboBox2.getSelectedIndex() == 1) {
                    choice = "Length";
                } else if (comboBox2.getSelectedIndex() == 2) {
                    choice = "Width";
                } else if (comboBox2.getSelectedIndex() == 3) {
                    choice = "Draft";
                }

                //displays the sorted data structure
                displayText.setText(textPrintSorted(choice));

            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayResources();
            }
        });
    }

    //function that gathers all of the data to be printed out to the gui using hashmaps
    public String textHashPrint(java.util.HashMap<Integer, Dock> docks, java.util.HashMap<Integer, Ship> ships, java.util.HashMap<Integer, SeaPort> seaPort,
                                java.util.HashMap<Integer, Person> persons, java.util.HashMap<Integer, Job> jobs) {
        StringBuilder result;
        result = new StringBuilder(">>>>> The world: \n\n\n");

        //uses hashmaps to get the tostrings
        for (SeaPort sea : seaPort.values()) {
            result.append(sea.toString());
            result.append("\n");
        }
        result.append("\n\n");

        for (Dock doc : docks.values()) {
            result.append(doc.toString());
            result.append("\n\n");
        }

        //adds of all the ships in que to string
        result.append("\n\n\n--- List of all ships in que: \n");

        for (Ship shi : ships.values()) {
            if (shi.getIndex() > 39999 && shi.getIndex() < 50000) {
                result.append(">");
                result.append(shi.toString());
                result.append("\n");
            }
        }


        //add all of the ships tostring to the string
        result.append("\n\n\n--- List of all ships: \n");


        for (Ship shi1 : ships.values()) {
            result.append(">");
            result.append(shi1.toString());
            result.append("\n");
        }

        //adds of people tostring to the string
        result.append("\n\n\n--- List of all persons: \n");

        for (Person per : persons.values()) {
            result.append(">");
            result.append(per.toString());
            result.append("\n");
        }

        result.append("\n---List of all jobs: \n");
        for (Job j : jobs.values()) {
            result.append(">");
            result.append(j.toString());
            result.append("\n");
        }


        //returns the string to be printed
        return result.toString();
    }
    //end text()

    //prints out the sorted data stucture
    public String textPrintSorted(String choice) {
        StringBuilder result;
        result = new StringBuilder("Sorted \n\n >>>>> The world: \n\n\n");

        //adds all of the ports tostring to the string
        for (int counter = 0; counter < world.ports.size(); counter++) {
            result.append(world.ports.get(counter).toString());
            result.append("\n");
        }
        result.append("\n\n");

        //adds all of the docks tostring to the string
        for (int counter = 0; counter < world.ports.size(); counter++) {

            result.append("\n\n");
            result.append("Port : ");
            result.append(world.ports.get(counter).getName());
            result.append("\n");
            for (int counter2 = 0; counter2 < world.ports.get(counter).docks.size(); counter2++) {
                result.append(world.ports.get(counter).docks.get(counter2).toString());
                result.append("\n\n");
            }
        }

        //splits up each list by the ports so it looks more sorted
        result.append("\n\n\n--- List of all ships in que, Sorted by ");
        result.append(choice);
        result.append(": \n");
        for (int counter = 0; counter < world.ports.size(); counter++) {
            result.append("\n\n");
            result.append("Port : ");
            result.append(world.ports.get(counter).getName());
            result.append("\n");
            for (int counter2 = 0; counter2 < world.ports.get(counter).que.size(); counter2++) {
                result.append(">");
                result.append(world.ports.get(counter).que.get(counter2).toString());
                result.append("\n");
            }
        }

        result.append("\n\n\n--- List of all ships: \n");
        for (int counter = 0; counter < world.ports.size(); counter++) {
            result.append("\n\n");
            result.append("Port : ");
            result.append(world.ports.get(counter).getName());
            result.append("\n");
            for (int counter2 = 0; counter2 < world.ports.get(counter).ships.size(); counter2++) {
                result.append(">");
                result.append(world.ports.get(counter).ships.get(counter2).toString());
                result.append("\n");
            }
        }

        result.append("\n\n\n--- List of all persons: \n");
        for (int counter = 0; counter < world.ports.size(); counter++) {
            result.append("\n\n");
            result.append("Port : ");
            result.append(world.ports.get(counter).getName());
            result.append("\n");
            for (int counter2 = 0; counter2 < world.ports.get(counter).persons.size(); counter2++) {
                result.append(">");
                result.append(world.ports.get(counter).persons.get(counter2).toString());
                result.append("\n");
            }
        }

        return result.toString();

    }

    public void displayResources() {
        StringBuilder result;
        result = new StringBuilder();

        //uses hashmaps to get all of the ports
        for (SeaPort sea : portList.values()) {
            int aviable = 0;
            result.append(sea.toString());
            result.append("\nWorkers: \n");
            //display the people that are in the ports
            for (int i = 0; i < sea.persons.size(); i++) {
                //says if they are in the port or working at a job
                if (!sea.persons.get(i).inUse) {
                    result.append("  ");
                    result.append(sea.persons.get(i));
                    result.append(" is at the port \n");
                    aviable++;
                } else {
                    result.append("  ");
                    result.append(sea.persons.get(i));
                    result.append(" is on the ship ");
                    if (shipList.containsKey(sea.persons.get(i).jobParent)) {
                        result.append(shipList.get(sea.persons.get(i).jobParent).getName());
                    }
                    result.append(" doing the job ");
                    result.append(sea.persons.get(i).job);
                    result.append("\n");
                }

            }
            //display total number of people waiting at the port
            result.append("\nThere are ");
            result.append(aviable);
            result.append(" workers that are waiting at the port\n\n");
        }

        resourceText.setText(String.valueOf(result));

    }




    public static void main(String[] args) {
        //creats gui
        JFrame frame = new JFrame("SeaPortGui");


        frame.setContentPane(new SeaPortGui().panelMain);
        //sets set of Gui
        frame.setSize(1000,800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);




    }

    //creats the panel for the threads
    private void createUIComponents() {
       progressPanel = new JPanel();
       progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
    }
}
