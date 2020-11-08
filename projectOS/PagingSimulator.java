package projectOS;

import java.util.Random;
import java.util.Scanner;

public class PagingSimulator {

  private String listOfOptions = "\n"
          + "\t0 -- Exit\n"
          + "\t1 -- Enter reference string\n"
          + "\t2 -- Generate reference string\n"
          + "\t3 -- Display current reference string\n"
          + "\t4 -- Simulate FIFO\n"
          + "\t6 -- Simulate LRU\n"
          + "\t7 -- Simulate LFU\n";

  private Scanner inputSc;
  private String CadenaDeReferencias;
  private int numFrames;
  private Frame[] frames;
  private char[] victims;
  private char[] MatrizDeFallos;
  private int ContadorDeFallas;

  /* debug mode
   * Assigns the reference string used in the week 6 homework
   * prints output all at once instead of step by step
   * prints applicable values along with the logical pages in each frame
   *    - FIFO: duration current page has been in the frame
   *    - OPT: distance to the next use of the current page
   *    - LRU: index of the last time current page was used
   *    - LFU: number of times current page has been used thus far
   */
  static boolean debugMode = false;

  public static void main(String[] args){
    new PagingSimulator();
  }

  public PagingSimulator(){
    init();
  }


  // initializes the main program loop that runs until the user quits
  private void init() {
    inputSc = new Scanner(System.in);
    int selection;


    System.out.print("Modo Debug? S o N:  ");
    if(inputSc.nextLine().trim().equalsIgnoreCase("s")) debugMode = true;

    // assign test ref string
    if(debugMode) CadenaDeReferencias = "2456153452365347356";

    // get number of physical frames
    while(true){
      System.out.print("Cuantos frames fisicos? (Max 7):  ");
      String temp = inputSc.nextLine().trim();
      if(temp.equals("")){
        System.out.println("**Debe ser un numero");
        continue;
      }

      int num;
      try{
        // verify input is a number
        num = Integer.parseInt(temp);
      } catch(NumberFormatException e) {
        System.out.println("**input invalido, debe ser un numero");
        continue;
      }

      // verify number is a valid selection
      if(num < 1 || num > 7){
        System.out.println("**seleccion invalida, de ser entre 1 y 7");
        continue;
      }

      numFrames = num;
      break;
    } // end while loop
    frames = new Frame[numFrames];

    // begin operation loop
    while((selection = getSelection()) != 0) {

      // if users selects one of the simulations, ensure a ref string has been assigned
      if(selection >= 4 && CadenaDeReferencias == null){
        System.out.println("**Primero debes introducir o generar una cadena de referencia");
        continue;
      }

      String temp;
      switch(selection){
        case 1:   // get a reference string from the user
          temp = readString();
          if(temp == null){
            continue;
          } else {
            CadenaDeReferencias = temp;
          }
          break;
        case 2:   // generate a reference string
          temp = generateString();
          if(temp == null){
            continue;
          } else {
            CadenaDeReferencias = temp;
          }
          break;
        case 3:   // display current reference string
          if(CadenaDeReferencias == null){
            System.out.println("**No existe la cadena de referencia");
          } else {
            System.out.println("Cadena actual:  " + CadenaDeReferencias);
          }
          break;
        case 4:
        case 6:
        case 7:
          initSim(selection);
      } // end switch
    } // end while loop
    // user has selected 0 to quit
    System.out.println("Exiting...");
  } // end init method


  // method that obtains the user's choice of the next operation
  private int getSelection() {
    String invalidInput = "\n**Input Invalido\n"
            + "**Introdusca un numero entre 0 al 7.\n";

    String input = "";
    int selection;
    while(true) {

      System.out.print("\nSeleccione alguna opcion:");
      System.out.print(listOfOptions);
      System.out.print("Seleccion:   ");

      // Obtain the integer the user entered
      try {
        input = inputSc.nextLine().trim();
        selection = Integer.parseInt(input);
      } catch (NumberFormatException e) {
        // if input = "" then the user has only pressed enter
        // In that case, do not print error message
        if(!input.equals("")) {
          System.out.println(invalidInput);
        }
        continue;
      }

      // make sure the integer is a valid option
      if(selection < 0 || selection > 7){
        System.out.println(invalidInput);
        continue;
      }

      return selection;
    } // end while loop

  } // end getSelection method


  // Gets a new reference string from the user
  private String readString(){
    while(true) {
      System.out.print("La nueva cadena debe esta formateada entre digitos del 0-9"
              + " y sin espacios. Ej: 0123857362\n"
              + "Nueva cadena:  ");
      String input = inputSc.nextLine().trim();
      if(input.equalsIgnoreCase("stop")){
        return null;
      } else if(input.equals("")){
        System.out.println("**La cadena no puede estar en blanco");
        continue;
      } else if(input.length() > 40) {
        System.out.println("**La cadena tiene un maximo de 40 caracteres");
        continue;
      }

      // test that each char is a digit
      try {
        for (int i = 0; i < input.length(); i++) {
          if (!Character.isDigit(input.charAt(i))) {
            throw new NumberFormatException();
          }
        }
      } catch(NumberFormatException e){
        System.out.println("**Cadena invalida\n"
                + "**La cadena debe esta formateada entre digitos del 0-9");
        continue;
      }

      return input;
    } // end while loop
  } // end readString


  // Gets string length from user then generates a random string of that length
  private String generateString(){
    String str = "";
    String input;
    int length;

    while(true) {
      // get string length
      System.out.print("Que tan larga la cadena debe ser? (Max 40)   ");
      input = inputSc.nextLine().trim();
      if (input.equals("")) {
        System.out.println("**Debe ser un numero");
        continue;
      }

      if(input.equalsIgnoreCase("stop")) return null;

      try{   // verify input is a number
        length = Integer.parseInt(input);
      } catch(NumberFormatException e ){
        System.out.println("**Input invalida\n"
                + "debe ser un numero");
        continue;
      }
      if( length > 40 ) {
        System.out.println("**la cantidad maxima es de 40");
        continue;
      }

      // generate the string
      Random rand = new Random();
      for(int i=0; i<length; i++){
        str += String.valueOf(rand.nextInt(10));
      }
      return str;
    } // end while loop
  } // end generateString

  /*
   * Begins the simulations by assigning pages until every frame is full -- this process
   * is the same for each simulation. The simulation methods populate the frame data structure.
   * If debug mode, the method to print the results entirely is called. Otherwise, the method
   * to print the results step by step is called.
   */
  private void initSim(int selection){
    victims = new char[CadenaDeReferencias.length()];
    MatrizDeFallos = new char[CadenaDeReferencias.length()];
    ContadorDeFallas = 0;
    int startIndex = initializeFrames();

    // Go ahead and print if the reference string is complete simply by
    // assigning a page to each frame.
    if(startIndex >= CadenaDeReferencias.length()){
      if(debugMode) printDebug(selection);
      else printResults();
      return;
    }

    String output = "";
    switch (selection){
      case 4:
        output += "First In First Out Algorithm";
        simFIFO(startIndex);
        break;
      case 6:
        output += "Least Recently Used Algorithm";
        simLRU(startIndex);
        break;
      case 7:
        output += "Least Frequently Used Algorithm";
        SimuladorLFU(startIndex);
        break;
    }
    System.out.println("--------------------------------------------");
    System.out.println(output);
    System.out.println("--------------------------------------------");
    if(debugMode) printDebug(selection);
    else printResults();
  } // end initSim

  // Simula paginación usando First in First out algorithm
  private void simFIFO(int IndiceDeComienzo){
    for(int i = IndiceDeComienzo; i< CadenaDeReferencias.length(); i++){
      char PaginaActual = CadenaDeReferencias.charAt(i);
      int Indice;

      // comprueba si la página ya está en un frame
      if((Indice = checkFrames(PaginaActual)) >= 0){
        step(Indice, PaginaActual);
        continue;
      }

      // determinar qué página del frame será reemplazada
      int FrameParaRemplazar = 0;
      for(int j=0; j<frames.length; j++){
        if(frames[j].getDuration() > frames[FrameParaRemplazar].getDuration()){
          FrameParaRemplazar = j;
        }
      }
      step(FrameParaRemplazar, PaginaActual);
      MatrizDeFallos[i] = 'F';
      ContadorDeFallas++;
    }
  } // Fin simFIFO


  // Simulates paging using a Least Recently Used algorithm
  private void simLRU(int startIndex){
    for(int i = startIndex; i< CadenaDeReferencias.length(); i++){
      char currentPage = CadenaDeReferencias.charAt(i);
      int index;

      // check if page is already in a frame
      if((index = checkFrames(currentPage)) >= 0){
        step(index, currentPage);
        continue;
      }

      // determine which frame's page will be replaced
      int frameToReplace = 0;
      for(int j=0; j<frames.length; j++){
        if(frames[j].getLastUsed() < frames[frameToReplace].getLastUsed()){
          frameToReplace = j;
        }
      }
      step(frameToReplace, currentPage);
      MatrizDeFallos[i] = 'F';
      ContadorDeFallas++;
    }
  } // end simLRU

  //Simulador LFU
  private void SimuladorLFU(int IndicedeInicio){
    for(int i = IndicedeInicio; i< CadenaDeReferencias.length(); i++){
      char PaginaActual = CadenaDeReferencias.charAt(i);
      int indice;
      // Checa si la pagina se encuentra dentro del frame
      if((indice = checkFrames(PaginaActual)) >= 0){
        step(indice, PaginaActual);
        continue;
      }

      // Determina cual frame de pagina esta actualmente
      int FrameARemplazar = 0;
      for(int j=0; j<frames.length; j++){
        if(frames[j].getFrequency() < frames[FrameARemplazar].getFrequency()){
          FrameARemplazar = j;
        }
      }
      step(FrameARemplazar, PaginaActual);
      MatrizDeFallos[i] = 'F';
      ContadorDeFallas++;
    }
  } // FIN LFU

  /**
   * Performs a step in the paging process. Updates each frame by either replacing its page
   * or by leaving the current page in the frame.
   * @param frameToReplace The index of the frame whose page is being replaced
   * @param value The page to be assigned to the frameToReplace
   */
  private void step(int frameToReplace, char value){
    for(int i=0; i<numFrames; i++){
      if(i == frameToReplace) {
        frames[i].updateValue(value);
      } else {
        frames[i].updateValue(frames[i].getValue());
      }
    }
  }

  // Processes the reference string until each frame contains a page
  // returns index where it left off
  private int initializeFrames(){
    // Create the frames
    for(int i=0; i<numFrames; i++){
      frames[i] = new Frame(this, CadenaDeReferencias);
    }

    int nextFrame = 0;
    int returnIndex = 0;
    for(int i = 0; i< CadenaDeReferencias.length(); i++){

      // break if all frames have been populated
      if(nextFrame >= numFrames){
        break;
      }

      // Check if the page already exists in a frame
      int index = checkFrames(CadenaDeReferencias.charAt(i));

      // if index >= 0 then the page exists in a frame already
      if(index >= 0){
        step(index, CadenaDeReferencias.charAt(i));
      } else {
        // otherwise assign the page to the next available frame
        step(nextFrame++, CadenaDeReferencias.charAt(i));
        MatrizDeFallos[i] = 'F';
        ContadorDeFallas++;
      }

      returnIndex++;
    } // end for loop

    return returnIndex;
  } // end initializeFrames

  /**
   * Searches the frames for the provided page
   * @param page the page to be searched for as a numeric character
   * @return returns the frame index if the page was found, otherwise returns -1
   */
  private int checkFrames(char page){
    for(int i=0; i<frames.length; i++){
      if(frames[i].getValue() == page){
        return i;
      }
    }
    return -1;
  }


  /* This print method is used for debug purposes.
   * The results are printed out all at once and an applicable measurement value is
   * printed for each frame and each step of the process depending on the algorithm used
   *    - FIFO: duration current page has been in the frame
   *    - OPT: distance to the next use of the current page
   *    - LRU: index of the last time current page was used
   *    - LFU: number of times current page has been used thus far
   */
  private void printDebug(int selection){
    int[] data = null;

    System.out.print("String :  ");
    // print the string
    for(int i = 0; i< CadenaDeReferencias.length(); i++){
      System.out.printf("%4c", CadenaDeReferencias.charAt(i));
    }
    String rule = "";
    for(int i = 0; i<(CadenaDeReferencias.length()*4)+11; i++){
      rule += "-";
    }
    System.out.println("\n" + rule);

    // print the frames
    for(int i=0; i<numFrames; i++){
      System.out.print("Frame " + i + ":  ");
      for(int j = 0; j< CadenaDeReferencias.length(); j++){
        System.out.printf("%4c",frames[i].getHistory(j));
      }
      switch (selection) {
        case 4:
          data = frames[i].durationHist;
          break;
        case 5:
          data = frames[i].distHist;
          break;
        case 6:
          data = frames[i].lastUsedHist;
          break;
        case 7:
          data = frames[i].frequencyHist;
          break;
      }

      if(data == null){
        System.out.println("**An unknown error has occurred");
        return;
      }
      System.out.print("\n          ");
      // print the applicable measurement value
      for (int j = 0; j < CadenaDeReferencias.length(); j++) {
        System.out.printf("%4d", data[j]);
      }
      System.out.println("\n");
    } // end outer for loop
    System.out.println(rule);
    System.out.print("Faults:   ");
    for(int i = 0; i< MatrizDeFallos.length; i++){
      System.out.printf("%4c", MatrizDeFallos[i]);
    }
    System.out.print("\nVictims:  ");
    // print the victims
    for(int i=0; i<victims.length; i++){
      System.out.printf("%4c", victims[i]);
    }
    System.out.println("\nTotal faults:  " + ContadorDeFallas);
    System.out.println();
  } // end printDebug


  /* This method prints the results step by step requiring the user to press enter
   * between steps.
   */
  private void printResults(){
    int stepCounter = 1;
    boolean stopFlag = false;

    while(!stopFlag){
      System.out.print("String :  ");
      // print the string
      for(int i = 0; i< CadenaDeReferencias.length(); i++){
        System.out.printf("%4c", CadenaDeReferencias.charAt(i));
      }

      // print a horizontal rule
      String rule = "";
      for(int i = 0; i<(CadenaDeReferencias.length()*4)+11; i++){
        if(i > (20*4)+11) break;
        rule += "-";
      }
      System.out.println("\n" + rule);

      // print the frames
      for(int i=0; i<numFrames; i++) {
        System.out.print("Frame " + i + ":  ");
        for (int j = 0; j < stepCounter; j++) {
          System.out.printf("%4c", frames[i].getHistory(j));
        }
        System.out.println();
      }

      System.out.println(rule);
      System.out.print("Faults:   ");
      for(int i=0; i<stepCounter; i++){
        System.out.printf("%4c", MatrizDeFallos[i]);
      }
      System.out.print("\nVictims:  ");
      // print the victims
      for(int i=0; i<stepCounter; i++){
        System.out.printf("%4c", victims[i]);
      }
      if(stepCounter++ >= CadenaDeReferencias.length()){
        stopFlag = true;
      }
      if(!stopFlag) {
        System.out.println("\nPress Enter to continue or type \"end\" to finish...");
        String input = inputSc.nextLine().trim();

        // set stepCounter so the all results print before stopping
        if (input.equalsIgnoreCase("end")) {
          stepCounter = CadenaDeReferencias.length();
        } else if(input.equalsIgnoreCase("stop")){
          return;
        }
      }

    } // end while loop
    System.out.println("\nTotal faults:  " + ContadorDeFallas);
    System.out.println();
  } // end printResults

  // updates the victim array
  // victims are reported from the frames themselves
  void reportVictim(char victim, int index){
    victims[index] = victim;
  }

}
