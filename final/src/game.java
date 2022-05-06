import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mysql.jdbc.Field;

public class game extends JPanel implements Runnable {
	private JLabel remain;
	private JLabel time;
    private ArrayList<Integer> field;
	private ArrayList<Integer> Remain;
	private Integer result;
	private Integer TimeRemain;
	private Integer MineRemain;
	private Integer last;
	private Long startTime;
	private int model;
	private static final int IMG_NUM=13;
	private static final int IMG_SIZE=15;
	private static final int GRID_ROW=16;
	private static final int GRID_COL=16;
	private Image[] Img;
	public void loadImg() {
		Img=new Image[IMG_NUM];
		for(int i=0;i<IMG_NUM;i++) {
			String path="pic/"+i+".png";
			ImageIcon icon=new ImageIcon(path);
			Img[i]=icon.getImage();
		}
	}
	private void updateGet() {
		TimeRemain=Remain.get(0);
		MineRemain=Remain.get(1);
		result=Remain.get(2);
	}
	private void updateSet() {
		Remain.set(0, TimeRemain);
		Remain.set(1, MineRemain);
		Remain.set(2, result);
	}

	public game() {
		addMouseListener(new MinesAdapter());
	}
	public void start(JLabel remain,JLabel time,ArrayList<Integer> field,ArrayList<Integer> Remain,int model) {
		loadImg();
		this.remain=remain;
		this.time= time;
	    this.field= field;
	    this.Remain=Remain;
	    updateGet();
	    this.last=TimeRemain;
		this.startTime = System.currentTimeMillis();
		this.model=model;
		new Thread(this).start();
	
	}
	public void beginGame() {
		
		if (model==0) {
			int count=0;
			while(count<MineRemain) {
				int j=(int) (Math.random()*GRID_ROW*GRID_COL);
				if(field.get(j)<=100) {
				field.set(j, field.get(j)+9);
				count++;}
			}
		for (int i=0;i<GRID_ROW*GRID_COL;i++) {
			if (field.get(i)<=100){
				int mine=0;
				int[] p= {i-GRID_ROW-1,i-GRID_ROW,i-GRID_ROW+1,i-1,i+1,i+GRID_ROW-1,i+GRID_ROW,i+GRID_ROW+1};
				if(p[0]>=0 &&p[0]%16!=15) { if (field.get(p[0])==109) mine++;}
				if(p[1]>=0) { if (field.get(p[1])==109) mine++;}
				if(p[2]>=0&&p[2]%16!=0) { if (field.get(p[2])==109) mine++;}
				if(p[3]%16!=15&&p[3]>=0) { if (field.get(p[3])==109) mine++;}
				if(p[4]%16!=0){ if (field.get(p[4])==109) mine++;}
				if(p[5]%16!=15&&p[5]/16<=15) { if (field.get(p[5])==109) mine++;}
				if(p[6]/16<=15) { if (field.get(p[6])==109) mine++;}
				if(p[7]/16<=15&&p[7]%16!=0) { if (field.get(p[7])==109) mine++;}
				field.set(i, field.get(i)+mine);	
			}
			}
		}
			
		}
		

	public void run() {
		beginGame();
		while(result.equals(0)) {
        repaint();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		}
		
		repaint();
		}
	
	
	public int ChoosePic(int index) {
		if(result.equals(-1)){
			if(field.get(index)==109||field.get(index)==9) {
				return 9;
		}
			else if (field.get(index)==20) {
				return 11;
			}
			else if (field.get(index)>=11&&field.get(index)<20) {
				return 12;
			}
		}
		if (field.get(index)>=100) {
		return 10;}
		else if (field.get(index)>=11 &&field.get(index)<=20) {
			return 11;
		}
		else {
			return field.get(index);
		}
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Long endTime = (System.currentTimeMillis()-startTime) / 1000;
		TimeRemain=last-endTime.intValue();
		time.setText("            Time Remaining:"+TimeRemain);
		if(TimeRemain.equals(0)) {
			result=-1;
			updateSet();
		}
		remain.setText(""+MineRemain);
		for(int i=0;i<GRID_ROW*GRID_COL;i++) {
			int index=ChoosePic(i);
			g.drawImage(Img[index], (i%GRID_COL*IMG_SIZE),(i/GRID_ROW*IMG_SIZE), this);
		}
		if (result.equals(1)) {remain.setText("YOU WIN");}
		else if(result.equals(-1)) {remain.setText("YOU LOSE");
		
		}
		updateSet();
	}
	public void uncover(int index) {
		int temp=field.get(index);
		if(temp>=100) {
		temp-=100; }
		if(temp>=11&&temp<20) 
		{temp-=11;
		MineRemain+=1;
		updateSet();}
		field.set(index, temp);
	}
	public void around(int index) {
		//if(field.get(index)<=8&&field.get(index)>=0) {return;}
		uncover(index);
		if(field.get(index)==0) {
			int i=index;
			int[] p= {i-GRID_ROW-1,i-GRID_ROW,i-GRID_ROW+1,i-1,i+1,i+GRID_ROW-1,i+GRID_ROW,i+GRID_ROW+1};
			if(p[0]>=0 &&p[0]%16!=15&&field.get(p[0])>8) {uncover(p[0]); around(p[0]);}
			if(p[1]>=0&&field.get(p[1])>8)   {uncover(p[1]);around(p[1]);}
			if(p[2]>=0&&p[2]%16!=0&&field.get(p[2])>8)   {uncover(p[2]);around(p[2]);}
			if(p[3]%16!=15&&p[3]>=0&&field.get(p[3])>8)   {uncover(p[3]);around(p[3]);}
			if(p[4]%16!=0&&field.get(p[4])>8)  {uncover(p[4]);around(p[4]);}
			if(p[5]%16!=15&&p[5]/16<=15&&field.get(p[5])>8)   {uncover(p[5]);around(p[5]);}
			if(p[6]/16<=15&&field.get(p[6])>8)  {uncover(p[6]);around(p[6]);}
			if(p[7]/16<=15&&p[7]%16!=0&&field.get(p[7])>8)   {uncover(p[7]);around(p[7]);}
			
		}
	}
    private class MinesAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
        	int i=(e.getX()/IMG_SIZE)+(e.getY()/IMG_SIZE)*GRID_ROW;
        	if (e.getButton() == MouseEvent.BUTTON1) {
        		if (field.get(i)>100) {
        		field.set(i, field.get(i)-100);
        		if(field.get(i)==9) {
        			result=-1;
        		}
        		}
        		else if (field.get(i)==100) {
        		around(i);
        		}
        	}
        	else if (e.getButton() == MouseEvent.BUTTON3) {
        		if (field.get(i)>=100 && MineRemain>0) {
        			field.set(i,11+field.get(i)-100);
        			MineRemain-=1;
        			
        		}
        		else if (field.get(i)>=11 && field.get(i)<=20) {
        			field.set(i,field.get(i)+100-11);
        			MineRemain+=1;
        		
        		}
        	}
        	if (MineRemain==0) {
        		boolean flag0=true;
        		for(int j=0;j<GRID_ROW*GRID_COL;j++) {
        			if(field.get(j)>=100) {
        				flag0=false;
        				break;
        			}
        		}
        		if(flag0) {
        			result=1;
        		}
        	}
        	updateSet();
        }
        }
}
