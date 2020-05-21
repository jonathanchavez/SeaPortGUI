import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


//Class thing
class Thing implements Comparable <Thing>{
    private int index;
    private String name;
    private int parent;

    public Thing(){

    }
    //sets index, name ,parent
    public Thing(String name, int index, int parent){
        this.index = index;
        this.name = name;
        this.parent = parent;
    }

    //sorts it by the name
    @Override
    public int compareTo(Thing o) {
        return this.name.compareTo(o.getName());
    }

    public String toString(){
        return name + " " + index;
    }

    public int getIndex() {
        return index;
    }
    public String getName(){
        return name;
    }

    public int getParent() {
        return parent;
    }
}

class World extends Thing{
    ArrayList<SeaPort> ports;
    PortTime time;

    public World(){
        ports = new ArrayList<SeaPort>();
    }

    public void Sort(){
        Collections.sort(ports);
    }


}

class SeaPort extends Thing{
    ArrayList<Dock> docks;
    ArrayList<Ship> que;
    ArrayList<Ship> ships;
    ArrayList<Person> persons;

    public SeaPort(String name, int index, int parent){

        super(name, index, parent);

        docks = new ArrayList<Dock>();
        que = new ArrayList<Ship>();
        ships = new ArrayList<Ship>();
        persons = new ArrayList<Person>();


    }
    public String toString(){
        return "SeaPort: " + super.toString();
    }

    //sorts all of the list
    public void compare(int queCheck){
        Collections.sort(persons);
        Collections.sort(docks);
        Collections.sort(ships);

        //find which one to call to sort the que
        if (queCheck == 0) {
            Collections.sort(que, Ship.shipWeightComp);
        }
        else if (queCheck == 1){
            Collections.sort(que, Ship.shipLengthComp);
        }
        else if (queCheck == 2){
            Collections.sort(que, Ship.shipWidthComp);
        }
        else if (queCheck == 3){
            Collections.sort(que, Ship.shipDraftComp);
        }



    }

}

class Dock extends Thing{
    Ship ship;
    int por;
    int job;
    int counter = 0;
    SeaPort port;
    ArrayList<Person> persons;

    public Dock(String name, int index, int parent, int por ){
        super(name, index, parent);

        this.por = por;
        ship = new Ship();
    }

    public void setPort(SeaPort port) {
        this.port = port;
        persons = port.persons;
    }

    //sets the ship to the ship in the dock and then sets the dock in the ship class to this dock
    public void setShip(Ship ship){
        this.ship = ship;
        ship.dock = this;
    }

    public void addCounter() throws InterruptedException {
        int temp =0;
        //adds to counter
        counter = counter + 1;
        //if all jobs had finish gets new ship from que
        if (counter == job){

            while (temp == 0) {
                //checks to see if any ship is in que
                if (port.que.size() > 0) {
                    setShip(port.que.get(0));
                    //remove from que and reset counter
                    port.que.remove(0);
                    counter = 0;
                    //gets the number of jobs from the ship
                    job = ship.jobs.size();
                    //checks to see if there is any jobs on the ship
                    if (!(counter == job)){
                        temp = 1;
                    }
                    //runs the jobs in the ship
                    ship.dock(this);
                }
            }



        }
    }

    public String toString(){
        return "Dock: " + super.toString() + "\n " + "Ship: " + ship.toString();
    }

}

class Ship extends Thing{
    PortTime arrivalTime,dockTime;
    public double draft, length, weight, width;
    ArrayList<Job> jobs;
    Dock dock = null;
    Semaphore sem = new Semaphore(1);

    public Ship(){

    }

    public Ship(String name, int index, int parent, double weight, double length, double width, double draft){
        super(name, index, parent);
        this.draft = draft;
        this.length = length;
        this.weight = weight;
        this.width = width;
        jobs = new ArrayList<Job>();


    }

    public void runJobs() throws InterruptedException {
        //gives the dock class the size of the job
        dock.job = jobs.size();
        //starts each job threads
        for (Job jo : jobs){
            jo.shipDock = dock;
            jo.sem = sem;
            jo.jobThread.start();
        }

    }

    //adds the different job to the arraylist jobs
    public void addJob(Job j){
        jobs.add(j);
    }
    public void dock(Dock dock) throws InterruptedException {
        //gets the dock and runs the threads
        this.dock = dock;
        runJobs();
    }

    //sorts it by different vairables
    public static Comparator<Ship> shipWeightComp = new Comparator<Ship>() {
        @Override
        public int compare(Ship o1, Ship o2) {

            return Double.compare(o1.weight, o2.weight);
        }
    };
    public static Comparator<Ship> shipLengthComp = new Comparator<Ship>() {
        @Override
        public int compare(Ship o1, Ship o2) {

            return Double.compare(o1.length, o2.length);
        }
    };

    public static Comparator<Ship> shipWidthComp = new Comparator<Ship>() {
        @Override
        public int compare(Ship o1, Ship o2) {

            return Double.compare(o1.width, o2.width);
        }
    };
    public static Comparator<Ship> shipDraftComp = new Comparator<Ship>() {
        @Override
        public int compare(Ship o1, Ship o2) {

            return Double.compare(o1.draft, o2.draft);
        }
    };




    public String toString(){
        return super.toString();
    }


}

class PassengerShip extends Ship{
    public int numberOfOccupiedRooms;
    public int numberOfPassengers;
    public int numberOfRooms;

    public PassengerShip(String name, int index, int parent, double weight, double length, double width, double draft,
                         int numPassengers, int numRooms, int numOccupied){

        super(name, index, parent, draft, length, weight, width);
        numberOfPassengers = numPassengers;
        numberOfRooms = numRooms;
        numberOfOccupiedRooms = numOccupied;

    }

    public String toString(){
        return "Passenger Ship: " + super.toString();
    }

}

class CargoShip extends Ship{
    double cargoValue;
    double cargoVolume;
    double cargoWeight;

    public CargoShip(String name, int index, int parent, double weight, double length, double width, double draft,
                     double cargoWeight, double cargoVolume, double cargoValue){
        super(name, index, parent, weight, length, width, draft);
        this.cargoWeight = cargoWeight;
        this.cargoVolume = cargoVolume;
        this.cargoValue = cargoValue;

    }


    public String toString(){
        return "Cargo Ship: " + super.toString();
    }
}

class Person extends Thing{
    String skill;
    boolean inUse;
    String job;
    int jobParent;

    public Person(String name, int index, int parent, String skill){
        super(name, index, parent);
        this.skill = skill;
        inUse = false;
        job = "";
        jobParent = 0;

    }

    public String getSkill() {
        return skill;
    }

    public String toString(){
        return "Person: " + super.toString() + " " + skill;
    }

}

class PortTime{
    int time;
}

class Job extends Thing implements Runnable{
    double duration;
    ArrayList<String> skills;
    JPanel main;
    Dock shipDock;
    Thread jobThread;
    JPanel barPanel = new JPanel();
    JButton stop = new JButton("Stop");
    JButton cancel = new JButton("Cancel");
    boolean cont = true;
    boolean canFlag = true;
    Semaphore sem;
    int sleepCounter = 0;

    public Job(String name, int index, int parent, double duration, ArrayList<String> skills, JPanel main){
        super(name, index, parent);
        this.duration = duration;
        this.skills = skills;
        this.main = main;
        //makes new thread
        jobThread = new Thread(this);

        //stop button
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contiune();
            }
        });

        //cancel button
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelThread();
            }
        });
    }
    public String toString() {
        return "Job: " + super.toString() + " " + skills.toString();
    }

    public void contiune(){
        //sets the boolean to opposite and change the button
        cont = !cont;
        if (cont) {
            stop.setText("Stop");

        }
        else {
            stop.setText("Go");
        }
    }

    public void cancelThread(){
        //changes boolean
        canFlag = !canFlag;
    }

    @Override
    public void run() {
        ArrayList<Integer> personTaken = new ArrayList<>();


        //makes new panel with progress bar, button and label
        main.add(barPanel);
        JProgressBar bar = new JProgressBar(0, (int) duration);
        bar.setStringPainted(true);
        JLabel status = new JLabel("Finding workers");
        barPanel.add(bar);
        barPanel.add(new JLabel(getName()));
        barPanel.add(status);
        barPanel.add(stop);
        barPanel.add(cancel);
        main.revalidate();


        //sets the time and duration of thread
        long startTime = System.currentTimeMillis();
        long time = startTime;
        long stopTime = (long) (startTime + (1000 * duration));


        int counting = 0;
        while (counting == 0) {
            try {
                //waits to get the lock
                status.setText("Waiting for dock");
                sem.acquire();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //enters in the synchrinized port where every job in a port goes through
            synchronized (shipDock.port) {
                //updates status on gui
                status.setText("Finding workers");
                ArrayList<String> tempChecker = new ArrayList<>();
                int checker = 0;
                //goes through each person at the port
                for (int i = 0; i < shipDock.persons.size(); i++) {

                    //checks to see if their skill match up to the job skills
                    for (String skill : skills) {
                        //looks to see if they are not already working on another job
                        if (skill.equals(shipDock.persons.get(i).skill) && !(shipDock.persons.get(i).inUse) && !(tempChecker.contains(skill))) {
                            tempChecker.add(skill);
                            //sets them to this job
                            shipDock.persons.get(i).inUse = true;
                            shipDock.persons.get(i).job = this.getName();
                            shipDock.persons.get(i).jobParent = this.getParent();
                            personTaken.add(i);
                            checker++;
                        }
                    }
                }
                //returns the people back to the port if the job doesn't have all of the neccesary skills
                if (!(checker == skills.size())) {

                    //sets the people aviable for other jobs
                    for (Integer integer : personTaken) {
                        shipDock.persons.get(integer).inUse = false;
                        shipDock.persons.get(integer).job = "";
                        shipDock.persons.get(integer).jobParent = 0;
                    }
                    tempChecker.clear();
                    personTaken.clear();

                } else {
                    //if have all the people require exits the loop
                    counting = 1;
                }


            }

            if (counting == 0) {
                //releases the permit
                sem.release();
                status.setText("Not enough workers");
                //goes to sleep for 75 percent of their duration of job and then retries to find people
                if (sleepCounter < 3) {
                    try {
                        long timeSleep = (long) ((duration * .75) * 1000);
                        Thread.sleep(timeSleep);
                        sleepCounter++;


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                if job cant find people in 3 tries exit program since there are not enough workers
                } else {
                    status.setText("Job cancel, not enough workers");
                    counting = 1;
                    try {
                        shipDock.addCounter();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        if (sleepCounter < 3) {
            status.setText("Waiting for dock");

            //checks to see if other threads are running in the same dock object
            synchronized (shipDock) {

                status.setText("Doing Job");
                try {
                    //checks to see if time is left and the cancel button hasn't been pressed
                    while (time < stopTime && canFlag) {
                        Thread.sleep(100);

                        //checks to see if stop button has been pressed
                        if (cont) {
                            //adds to time and update progress bar
                            time = time + 100;
                            bar.setValue((int) (time - startTime) / 1000);
                        }
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //when done with job, releases the people back to the port
                for (Integer integer : personTaken) {
                    shipDock.persons.get(integer).inUse = false;
                    shipDock.persons.get(integer).job = "";
                    shipDock.persons.get(integer).jobParent = 0;
                }
                shipDock.notifyAll();
                status.setText("Job Completed");
                //tells the dock that this thread has finish
                try {
                    shipDock.addCounter();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //release permit
            sem.release();

        }
    }
}
