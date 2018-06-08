package domain.filters.formatters;

import domain.filters.IValuePostFormatter;

import java.util.ArrayList;
import java.util.List;

public class NumberValuePostFormatter implements IValuePostFormatter {

    List<Character> innerNumberChars;
    private NumberValueFormatPolicy policy;

    public NumberValuePostFormatter(NumberValueFormatPolicy policy) {
        this.innerNumberChars = new ArrayList<>();
        this.innerNumberChars.add('.');
        this.innerNumberChars.add(',');
        this.policy = policy;
    }

    @Override
    public List<String> Extract(String value) {
        List<String> res = new ArrayList<>();
        boolean recording = false;
        String recordedNumber = "";
        value += "E";
      //  char lastChar = '\0';
        for (int i = 0; i < value.length(); i++){
            char c = value.charAt(i);
            if(this.isNumberFinishedParsing(c, recording)){
               // if(!this.isInvalidNumber(lastChar)){
                    res.add(recordedNumber);
                //}
                recording = false;
                recordedNumber = "";
            }
            else if( this.isNumberCurrentlyParsing(c, recording)){
                recordedNumber += c;
              //  lastChar = c;
            }else if(this.isNumberAboutToBeParsed(c, recording)){
                recording = true;
                recordedNumber+= c;
             //   lastChar = c;
            }
        }
        return res;
    }

    private boolean isInvalidNumber(char lastNumberChar) {
        if(this.innerNumberChars.contains(lastNumberChar))
            return true;
        return false;
    }

    private boolean isNumberFinishedParsing(char c, boolean recording) {
        if(recording && !Character.isDigit(c) && !this.innerNumberChars.contains(c))
            return true;
        return false;
    }

    private boolean isNumberAboutToBeParsed(char c, boolean recording) {
        if(!recording && Character.isDigit(c))
            return true;
        return false;
    }

    private boolean isNumberCurrentlyParsing(char c, boolean recording) {
        return (Character.isDigit(c) && recording) || (this.innerNumberChars.contains(c) && recording);
    }

    @Override
    public String Format(String number) {
        switch (this.policy){
            case CONVERT_COMMA_TO_DOT:
                return number.replace(',', '.');
            case CONVERT_DOT_TO_COMMA:
                return number.replace('.', ',');
            default:
                return number;
        }
    }

}
