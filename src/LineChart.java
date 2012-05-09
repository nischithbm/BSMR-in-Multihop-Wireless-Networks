import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
 
public class LineChart extends JPanel {
	double[] dataY = {  100, 98.2, 96.5, 94.7, 89.2};
	double[] dataX = { 0, 16, 25 ,30, 65};
	final int PAD  = 70;
	final int PAD2  = PAD;			//120;
    final int TICK  = 2;
    final int STICK = 3;
 
    String xLabel = "No. of Adversaries";
    String yLabel = "Packet Delivery Ratio(%)";
    
    
    private void label_rotate(Graphics g, double x, double y, double theta, String label) { 
        Graphics2D g2D = (Graphics2D)g; 
        
       AffineTransform fontAT = new AffineTransform(); 		// Create a rotation transformation for the font. 
       Font theFont = g2D.getFont(); 						 // get the current font 
       fontAT.rotate(theta); 								// Derive a new font using a rotatation transform 
       Font theDerivedFont = theFont.deriveFont(fontAT); 	 // set the derived font in the Graphics2D context       
       g2D.setPaint(Color.black);
       g2D.setFont(theDerivedFont); 
       g2D.drawString(label, (int)x, (int)y); 				// Render a string using the derived font 
       g2D.setFont(theFont); 								// put the original font back 
   } 
   
    private void plotData(Graphics g, double[] data,Color clr) { 
    	 Graphics2D g2 = (Graphics2D)g;
    Point2D.Double lastP = null;
    for(int j = 0; j < data.length; j++) {
        g2.setPaint(Color.red);
        Point2D.Double p = null;
        if(j>0){
        	p = modelToView(dataX[j]/10, data[j]);
        }
        else if(j==0){
        	p = modelToView(j, data[j]);
        }
        g2.fill(new Ellipse2D.Double(p.x-2, p.y-2, 4, 4));
       
        g2.drawString(String.valueOf(data[j]), (int)p.x+6,(int) p.y-4);
        if(lastP != null) {
            g2.setPaint(clr);
            g2.draw(new Line2D.Double(lastP, p));
        }
        lastP = p;
    }
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        drawAxes(g2);
        
        // specify axes labels
        g2.drawString(xLabel, (getWidth()/3)+(getWidth()/10), (int)(getHeight()-(PAD2/2)));
        label_rotate(g,PAD/2, (getHeight()/2)+(getHeight()/6), 270 * java.lang.Math.PI/180,yLabel);
         
        
        
        // plot data
       plotData(g,dataY,Color.blue);
     
        //   plotData(g,data2,Color.green);
        
        
       
       
    }
 
    private Point2D.Double modelToView(double x, double y) {
        double w = getWidth();
        double h = getHeight();
        double tx = PAD;
        double ty = h-PAD2;
        AffineTransform at = AffineTransform.getTranslateInstance(tx, ty);
       
        int maxX = (int)Math.ceil(getMaxValue());
        double xScale = ((w - 2*PAD)/(maxX/10));
        int maxY = 100;
        double yScale = (h - 2*PAD2)/maxY;
        at.scale(xScale, -yScale);
        Point2D.Double dst = new Point2D.Double();
        at.transform(new Point2D.Double(x, y), dst);
        return dst;
    }
 
    private void drawAxes(Graphics2D g2) {
        double w = getWidth();
        double h = getHeight();
        
        int maxY = 100;
        int maxX = (int)Math.ceil(getMaxValue());
        double xInc = ((w - 2*PAD)/(maxX/10));
        double yInc = (h - 2*PAD2)/maxY;
        // grid lines
        g2.setPaint(new Color(220,230,240));
        // vertical
        for(int j = 0,k=0; j <= maxX; k++,j=j+10) {
            double x = PAD + k*xInc;
            g2.draw(new Line2D.Double(x, PAD2, x, h-PAD2));
        }
        // horizontal
        for(int j = 0; j <= maxY; j=j+10) {
            double y = PAD2 + j*yInc;
            g2.draw(new Line2D.Double(PAD, y, w-PAD, y));
        }
        // axes
        g2.setPaint(new Color(51,51,51));
 
        // ordinate
        g2.draw(new Line2D.Double(PAD, PAD2, PAD, h-PAD2));
        // tick marks
        for(int j = 0; j <= maxY; j=j+10) {
            double y = PAD2 + j*yInc;
            g2.draw(new Line2D.Double(PAD, y, PAD-TICK, y));
        }
        // labels
        Font font = g2.getFont().deriveFont(14f);
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        for(int j = 0,k=0; j <= maxY; j=j+10,k++) {
            String s = String.valueOf(maxY-j);
            float y = (float)(PAD2 + j*yInc + lm.getDescent());
            float width = (float)font.getStringBounds(s, frc).getWidth();
            float x = (float)(PAD - TICK - STICK - width);
            g2.drawString(s, x, y);
        }
        
        // abcissa
        g2.draw(new Line2D.Double(PAD, h-PAD2, w-PAD, h-PAD2));
        // tick marks
        for(int j = 0,k=0; j <= maxX; k++,j=j+10) {
            double x = PAD + k*xInc;
            g2.draw(new Line2D.Double(x, h-PAD2, x, h-PAD2+TICK));
        }
        // labels
        for(int j = 0,k=0; j <= maxX;k++, j=j+10) {
            String s = String.valueOf(j);
            float width = (float)font.getStringBounds(s, frc).getWidth();
            float x = (float)(PAD + k*xInc - width/2);
            float y = (float)(h-PAD2 + TICK + STICK + lm.getAscent());
            g2.drawString(s, x, y);
        }
    }
 
    
    
    private double getMaxValue() {
        double max = -Double.MAX_VALUE;
        int j=0;
        for(j = 0; j < dataX.length; j++) {
            if(dataX[j] > max)
                max = dataX[j];
        }
        
       for(j=0;j<max;j+=10){
       }
              
        return j;
    }
 
    
    
    public static void chart(){
    	JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.getContentPane().add(new LineChart());
        f.setMinimumSize(new Dimension(500,400));
        f.setSize(600,450);
        f.setLocation(250,100);
        f.setVisible(true);
    }
    
}