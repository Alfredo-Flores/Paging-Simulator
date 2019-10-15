package projectOS;

/**
 * The Frame class is used in the PagingSimulator program. It represents a physical frame that
 * contains a logical page. It records the page history throughout the simulation. If in debug
 * mode, it will record the particular indicator used for the algorithm at each page.
 */
class Frame {

  private PagingSimulator owner;
  private char value;
  private char[] history;
  private int duration;
  private String refString;
  private int distToNextUse;
  private int lastUsed;
  private int frequency;
  private int currentIndex;

  // debugging
  private static boolean debugMode = PagingSimulator.debugMode;
  int[] durationHist;
  int[] distHist;
  int[] lastUsedHist;
  int[] frequencyHist;

  Frame(PagingSimulator owner, String refString){
    history = new char[refString.length()];
    this.owner = owner;
    this.refString = refString;
    this.value = ' ';
    currentIndex = 0;

    if(debugMode) {
      durationHist = new int[refString.length()];
      distHist = new int[refString.length()];
      lastUsedHist = new int[refString.length()];
      frequencyHist = new int[refString.length()];
    }
  }

  /**
   * Updates the frame's value with the provided value at the current index.
   * Also updates all related indicators
   * @param value value to assign to that position
   */
  void updateValue(char value){
    // updates duration page has been in frame
    if(this.value == value){
      duration++;
    } else {
      // reports a victim to the OS that owns this frame
      owner.reportVictim(this.value, currentIndex);
      duration = 0;
    }
    if(debugMode) durationHist[currentIndex] = duration;    // debug

    // update value and add to history
    this.value = value;
    history[currentIndex] = value;

    // update dist to next use
    distToNextUse = refString.length();
    for(int i=currentIndex+1; i<refString.length(); i++){
      if(refString.charAt(i) == value){
        distToNextUse = i - currentIndex;
        break;
      }
    }
    if(debugMode) distHist[currentIndex] = distToNextUse;   // debug

    // update last used
    lastUsed = currentIndex;
    for(int i=currentIndex; i>=0; i--) {
      if(refString.charAt(i) == value){
        lastUsed = i;
        break;
      }
    }
    if(debugMode) lastUsedHist[currentIndex] = lastUsed;   // debug

    // update frequency
    frequency = 0;
    for(int i=currentIndex; i>=0; i--){
      if(refString.charAt(i) == value) frequency++;
    }
    if(debugMode) frequencyHist[currentIndex] = frequency;    // debug

    currentIndex++;
  }

  // returns the frame's page for the provided index
  char getHistory(int index){
    return history[index];
  }

  char getValue(){
    return value;
  }

  int getDuration(){
    return duration;
  }

  int getDistToNextUse(){
    return distToNextUse;
  }

  int getLastUsed(){
    return lastUsed;
  }

  int getFrequency(){
    return frequency;
  }

}
