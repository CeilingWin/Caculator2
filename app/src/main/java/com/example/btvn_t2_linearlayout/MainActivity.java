package com.example.btvn_t2_linearlayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Stack;

import static android.text.Html.fromHtml;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    enum OperationE {PLUS,DIVIDE,MULTI,SUB}
    private TextView result_view;
    private TextView input_view;
    private StringBuilder result_string;
    private StringBuilder input_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.result_view=(TextView) findViewById(R.id.result);
        this.input_view=(TextView)findViewById(R.id.input);
        this.input_string=new StringBuilder();
        this.result_string=new StringBuilder("0");

        Button xbp=(Button) findViewById(R.id.btn_xbp);
        Button cbh=(Button) findViewById(R.id.btn_cbh);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            xbp.setText(Html.fromHtml(getString(R.string.btn_xbp),Html.FROM_HTML_MODE_COMPACT));
            cbh.setText(Html.fromHtml(getString(R.string.btn_cbh),Html.FROM_HTML_MODE_COMPACT));
        }else{
            xbp.setText(Html.fromHtml(getString(R.string.btn_xbp)));
            cbh.setText(Html.fromHtml(getString(R.string.btn_cbh)));
        }
        this.updateOnClick();
    }
    private void updateOnClick(){
        findViewById(R.id.btn_0).setOnClickListener(this.handleNumber);
        findViewById(R.id.btn_1).setOnClickListener(this.handleNumber);
        findViewById(R.id.btn_2).setOnClickListener(this.handleNumber);
        findViewById(R.id.btn_3).setOnClickListener(this.handleNumber);
        findViewById(R.id.btn_4).setOnClickListener(this.handleNumber);
        findViewById(R.id.btn_5).setOnClickListener(this.handleNumber);
        findViewById(R.id.btn_6).setOnClickListener(this.handleNumber);
        findViewById(R.id.btn_7).setOnClickListener(this.handleNumber);
        findViewById(R.id.btn_8).setOnClickListener(this.handleNumber);
        findViewById(R.id.btn_9).setOnClickListener(this.handleNumber);
        findViewById(R.id.btn_plus).setOnClickListener(this);
        findViewById(R.id.btn_sub).setOnClickListener(this);
        findViewById(R.id.btn_multi).setOnClickListener(this);
        findViewById(R.id.btn_divide).setOnClickListener(this);
        findViewById(R.id.btn_equal).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_ce).setOnClickListener(this);
    }

    private View.OnClickListener handleNumber=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button b=(Button) findViewById(v.getId());
            String number=b.getText().toString();
            if (input_string.length()==0 && number.charAt(0)=='0') return;
            if (input_string.length()>0 && number.charAt(0)=='0' && !Character.isDigit(input_string.charAt(input_string.length()-1))) return;
            updateInput(b.getText().toString());
        }
    };
    @Override
    public void onClick(View v) {
        String op="";
        switch (v.getId()) {
            case R.id.btn_plus:
                op="+";
                break;
            case R.id.btn_sub:
                op="-";
                break;
            case R.id.btn_multi:
                op="x";
                break;
            case R.id.btn_divide:
                op=":";
                break;
            case R.id.btn_equal:
                this.handleEqualButton();
                return;
            case R.id.btn_delete:
                this.deleteInput();
                return;
            case R.id.btn_ce:
                this.clearInput();
                return;
        };
        if (this.input_string.length()!=0 && Character.isDigit(this.input_string.charAt(input_string.length()-1))){
            this.updateInput(op);
        }
    }
    private void updateInput(String ip){
        this.input_string.append(ip);
        this.input_view.setText(this.input_string);
        if (Character.isDigit(ip.charAt(0)))
            this.updateResult();
    }
    private void deleteInput(){
        if (this.input_string.length()>=1){
            this.input_string=new StringBuilder(this.input_string.substring(0,input_string.length()-1));
            this.input_view.setText(input_string);
            if (input_string.length()!=0 && !Character.isDigit(input_string.charAt(input_string.length()-1))) return;
            this.updateResult();
        }
    }
    private void clearInput(){
        this.input_string=new StringBuilder("");
        this.result_string=new StringBuilder("0");
        this.input_view.setText(input_string);
        this.result_view.setText(result_string);
    }
    private void updateResult(){
        if (this.input_string.length()!=0){
            ArrayList<String> postfix=convertToPostfix(this.input_string.toString());
            Double result=caculator(postfix);
            NumberFormat nf=NumberFormat.getInstance();
            this.result_string=new StringBuilder(result.toString());
            this.result_view.setText(nf.format(result));
        }
        else{
            this.result_string=new StringBuilder("0");
            this.result_view.setText(result_string);
        }
    }
    private static ArrayList<String> convertToPostfix(String infix){
        ArrayList<String> result= new ArrayList<>();
        Stack<String> numbers=new Stack<>();
        Stack<String> operators=new Stack<>();
        while (infix.length()!=0){
            if (Character.isDigit(infix.charAt(0))){
                int i=1;
                while(i<infix.length()&& (Character.isDigit(infix.charAt(i))|| infix.charAt(i)=='.')) i++;
                String number=infix.substring(0,i);
                result.add(number);
                infix=infix.substring(i);
            }
            else{
                String operator=infix.substring(0,1);
                infix=infix.substring(1);
                while (!operators.isEmpty() && checkRank(operator)<=checkRank(operators.lastElement())){
                    String outOperator=operators.pop();
                    result.add(outOperator);
                }
                operators.push(operator);
            }
        }
        while (!operators.isEmpty()){
            result.add(operators.pop());
        }
        return result;
    }
    private static int checkRank(String op){
        Character cop=op.charAt(0);
        switch(cop){
            case '+':
            case '-':
                return 1;
            case 'x':
            case ':':
                return 2;
            default:
                return 0;
        }
    }
    private static Double caculator(ArrayList<String> postfix){
        Double result=0.d;
        Stack<Double> stack=new Stack<>();
        stack.push(0.d);
        for (String tt:postfix){
            if (Character.isDigit(tt.charAt(0))){
                Double number=Double.parseDouble(tt);
                stack.push(number);
            }
            else{
                Double secondtNumber=stack.pop();
                Double firstNumber=stack.pop();
                Double rs;
                switch(tt.charAt(0)){
                    case '+':rs=secondtNumber+firstNumber;break;
                    case '-':rs=firstNumber-secondtNumber;break;
                    case 'x':rs=firstNumber*secondtNumber;break;
                    case ':':rs=firstNumber/secondtNumber;break;
                    default:rs=0.d;
                }
                stack.push(rs);
            }
        }
        result=stack.pop();
        return result;
    }
    private void handleEqualButton(){
        this.input_string=this.result_string;
        if (input_string.length()==1 && input_string.charAt(0)=='0')
            this.input_string= new StringBuilder("");
        this.input_view.setText(this.input_string);
        this.result_view.setText("");
    }
}