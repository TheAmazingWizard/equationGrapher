/*
* @author Vishnu Bharath
*
*
*
* */



import org.nfunk.jep.JEP;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EquationGrapher extends JFrame {
    private JTextField equationField;
    private GraphPanel graphPanel;
    public JEP myParser = new org.nfunk.jep.JEP(); //equation parser initialization

    // this is my equationdata object
    // it has two fields, a color, and the equation string
    private static class EquationData {
        String equation;
        Color color;

        public EquationData(String equation, Color color) {
            this.equation = equation;
            this.color = color;
        }
    }

    //arraylist to store equations using equationdata object
    private final java.util.List<EquationData> equations = new java.util.ArrayList<>();


    public EquationGrapher() {

        //this is some parser setup, reference JEP documentation to learn more
        myParser.setImplicitMul(true);
        // Load the standard functions
        myParser.addStandardFunctions();
        // Load the standard constants, and complex variables/functions
        myParser.addStandardConstants();
        myParser.addComplex();

        //jframe setup
        setTitle("Equation Grapher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 1400);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel();

        // Label for the equation field with "y=" for user understanding
        JLabel equationLabel = new JLabel("y= ");
        equationField = new JTextField(20);

        // Drop-down menu for function syntax
        String[] functionSyntax = {
                "Functions, suffix of (x)",
                "abs - absolute value",
                "acos - inverse cosine",
                "acosh - inverse hyperbolic cosine",
                "arg - argument (phase angle) of complex number",
                "asin - inverse sine",
                "asinh - inverse hyperbolic sine",
                "atan - inverse tangent",
                "atan2 - angle between positive x-axis and vector (x, y)",
                "atanh - inverse hyperbolic tangent",
                "avg - average of values",
                "binom - binomial coefficient",
                "ceil - smallest integer greater than or equal to argument",
                "cmod - complex modulus",
                "complex - construct complex number",
                "conj - complex conjugate",
                "cos - cosine",
                "cosec - cosecant (reciprocal of sine)",
                "cosh - hyperbolic cosine",
                "cot - cotangent (reciprocal of tangent)",
                "exp - exponential function",
                "floor - largest integer less than or equal to argument",
                "if - conditional statement",
                "im - imaginary part of complex number",
                "lg - logarithm base 10",
                "ln - natural logarithm",
                "log - logarithm",
                "max - maximum value",
                "min - minimum value",
                "mod - modulus",
                "polar - convert to polar form",
                "pow - exponentiation",
                "rand - random number",
                "re - real part of complex number",
                "rint - round to nearest integer",
                "round - round to nearest integer or decimal place",
                "sec - secant (reciprocal of cosine)",
                "signum - sign function",
                "sin - sine",
                "sinh - hyperbolic sine",
                "sqrt - square root",
                "str - convert to string",
                "sum - summation of values",
                "tan - tangent",
                "tanh - hyperbolic tangent",
                "vsum - vector sum"
        };
        JComboBox<String> syntaxDropdown = new JComboBox<>(functionSyntax);

        //dropdown menu for operator syntax
        String[] operatorDescriptions = {
                "Operators",
                "\"^-1\": operator for inverse (reciprocal)",
                "\"^\": Exponentiation operator",
                "\"!\": Logical NOT operator",
                "\"-\": minus operator (negation)",
                "\"+\": plus operator (no operation)",
                "\"*\", \"×\": Multiplication operator",
                "\"/\", \"÷\": Division operator",
                "\"%\": Modulus operator (remainder of division)",
                "\".\": Member access operator (e.g., object.method())",
                "\"^^\": Exponentiation operator with higher precedence",
                "\"+\": Addition operator",
                "\"-\", \"−\": Subtraction operator",
                "\">\": Greater than operator",
                "\"<\": Less than operator",
                "\"<=\": Less than or equal to operator",
                "\">=\": Greater than or equal to operator",
                "\"==\": Equality operator",
                "\"!=\": Inequality operator",
                "\"&&\": Logical AND operator",
                "\"||\": Logical OR operator",
                "\"=\": Assignment operator"
        };
        JComboBox<String> operatorDropdown = new JComboBox<>(operatorDescriptions);

        //other fields
        equationField = new JTextField(20); //equation entry box
        JButton graphButton = new JButton("Graph"); //graph button to initiate graphing
        JButton colorButton = new JButton("Choose Color"); //choose color button to open color dialog

        //graph button listener -> if user clicks, will regraph
        graphButton.addActionListener(e -> {
            Color graphColor = Color.blue;
            if (graphColor != null) {
                if (equations.size() == 1) {
                    equations.remove(0);
                }
                equations.add(new EquationData(equationField.getText(), graphColor));
                graphPanel.repaint();
            }
        });

        //color button listener -> if user clicks, will change color and regraph
        colorButton.addActionListener(e -> {
            Color graphColor = JColorChooser.showDialog(this, "Choose Graph Color", Color.BLUE);
            if (graphColor != null) {
                equations.set(0, new EquationData(equationField.getText(), graphColor));
                graphPanel.repaint();
            }
        });



        //add the elements to the jpanel
        panel.add(equationLabel);
        panel.add(equationField);
        panel.add(graphButton);
        panel.add(colorButton);
        panel.add(syntaxDropdown);
        panel.add(operatorDropdown);

        graphPanel = new GraphPanel();
        add(panel, "North");
        add(graphPanel, "Center");


        add(panel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
    }

    private class GraphPanel extends JPanel {

        //draw graph method - call to output to canvas
        private void drawGraph(Graphics2D g2d, String equation) {

            //padding and scale for drawing the graph
            int width = getWidth();
            int height = getHeight();
            int padding = 40;
            int graphLeft = padding;
            int graphRight = width - padding;
            int graphTop = padding;
            int graphBottom = height - padding;
            int maxScale = 10;

            //evaluating the equation for every x value, and displaying it
            //keeping padding and scale in mind
            for (int x = graphLeft; x <= graphRight; x++) {
                    double xValue = (double) (x - graphLeft - (graphRight - graphLeft) / 2) / ((graphRight - graphLeft) / 2) * maxScale;
                    double yValue = evaluateEquation(equation, xValue);

                    int xPixel = x;
                    int yPixel = (int) (graphTop + (graphBottom - graphTop) / 2 - (yValue / maxScale * (graphBottom - graphTop) / 2));

                    if (x == graphLeft) {
                        g2d.drawLine(xPixel, yPixel, xPixel, yPixel);
                    } else {
                        int prevXPixel = x - 1;
                        double prevXValue = (double) (prevXPixel - graphLeft - (graphRight - graphLeft) / 2) / ((graphRight - graphLeft) / 2) * maxScale;
                        double prevYValue = evaluateEquation(equation, prevXValue);
                        int prevYPixel = (int) (graphTop + (graphBottom - graphTop) / 2 - (prevYValue / maxScale * (graphBottom - graphTop) / 2));

                        g2d.draw(new Line2D.Double(prevXPixel, prevYPixel, xPixel, yPixel));
                    }
                }
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 40;
            int graphLeft = padding;
            int graphRight = width - padding;
            int graphTop = padding;
            int graphBottom = height - padding;

            // Draw axes
            g2d.setColor(Color.BLACK);
            g2d.drawLine(graphLeft, graphTop + (graphBottom - graphTop) / 2, graphRight, graphTop + (graphBottom - graphTop) / 2); // X-axis
            g2d.drawLine(graphLeft + (graphRight - graphLeft) / 2, graphTop, graphLeft + (graphRight - graphLeft) / 2, graphBottom); // Y-axis

            // Draw scales
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            int maxScale = 10;
            for (int i = -maxScale; i <= maxScale; i++) {
                int x = graphLeft + (i + maxScale) * (graphRight - graphLeft) / (2 * maxScale);
                int y = graphTop + (graphBottom - graphTop) / 2 + 10;
                if (i != 0) {
                    g2d.drawString(String.valueOf(i), x, y);
                }

                y = graphTop + (maxScale - i) * (graphBottom - graphTop) / (2 * maxScale);
                x = graphLeft + (graphRight - graphLeft) / 2 - (i == 0 ? 10 : 20);
                if (i != 0) {
                    g2d.drawString(String.valueOf(i), x, y);
                }
            }

            //drawing each equation in arraylist -> should only be 1
            for (EquationData data : equations) {
                g2d.setColor(data.color);
                drawGraph(g2d, data.equation);
            }
            }

        //evaluting the equation by passing it into the parser
        private double evaluateEquation(String equation, double x) { //evaluate equation method, plugs x value into equation
            myParser.addVariable("x", x); //setup
            myParser.parseExpression(equation); //parsing here
            System.out.println(myParser.getValue());
            return myParser.getValue();
        }



        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 300);
        }
    }

    public static void main(String[] args) {
        EquationGrapher grapher = new EquationGrapher();
        grapher.setVisible(true);
    }
}