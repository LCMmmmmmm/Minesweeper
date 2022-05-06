import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;


public class Minesweeper extends JFrame{
	private JLabel remain;
	private JLabel time;
    private ArrayList<Integer> field;
	private Connection conn;
	private String sql;
	private Statement stmt;
	private int id=0;
	private String TR="            Time Remaining:";
	private Integer TimeRemain=1000;
	private Integer MineRemain=40;
	private Integer Result=0;
	private ArrayList<Integer> Remain;
	private game g;
	private static final String url = "jdbc:mysql://rm-0xi4a86cm08l6a9k2eo.mysql.rds.aliyuncs.com:3306?zeroDateTimeBehavior=convertToNull&"
            + "user=yl7544&password=Liyueyi961117&useUnicode=true&characterEncoding=UTF8&useSSL=false";
	private static final int IMG_SIZE=15;
	private static final int GRID_ROW=16;
	private static final int GRID_COL=16;
	private static final int FRAME_WIDTH = IMG_SIZE*GRID_COL;
	private static final int FRAME_HEIGHT = 320;
	
	public Minesweeper() {
		try {
	        Class.forName("com.mysql.jdbc.Driver");   
	        conn = DriverManager.getConnection(url);
	        stmt = conn.createStatement();
	        String sqlusedb="use minesweeper_yl7544";
	        stmt.executeUpdate(sqlusedb);
	        sql="SELECT max(id) FROM minesweeper";
	        ResultSet rs = stmt.executeQuery(sql);
	        rs.next();
	        id=Integer.parseInt(rs.getString(1));
	        conn.close();
	        
		} catch (SQLException e) {
			System.err.println("Connection error: " + e);
			System.exit(1);
		} 
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		field=new ArrayList<Integer>();
		for(int i=0;i<GRID_ROW*GRID_COL;i++) {
			field.add(100);
		}
		Remain=new ArrayList<Integer>();
		Remain.add(1000);
		Remain.add(40);
		Remain.add(0);
        setTitle("Minesweeper");
        createMenus();
        remain = new JLabel(""+MineRemain);
        add(remain, BorderLayout.SOUTH);
        time = new JLabel(TR+TimeRemain);
        add(time, BorderLayout.NORTH);
        g= new game();
        g.start(remain,time,field,Remain,0);
        add(g,BorderLayout.CENTER);
        id+=1;
    


	}
	private void updateGet() {
		TimeRemain=Remain.get(0);
		MineRemain=Remain.get(1);
		Result=Remain.get(2);
	}
	private void updateSet() {
		Remain.set(0, TimeRemain);
		Remain.set(1, MineRemain);
		Remain.set(2, Result);
	}
	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();     
	    setJMenuBar(menuBar);
	    JMenu menu = new JMenu("File");
	    JMenuItem item_e = new JMenuItem("Exit");    
	    class MenuItemListener implements ActionListener
	    {
	       public void actionPerformed(ActionEvent event)
	       {
	          System.exit(0);
	       }
	    }      
	    ActionListener listener_e = new MenuItemListener();
	    item_e.addActionListener(listener_e);
	    
	    JMenuItem item_o = new JMenuItem("Open");
		class OpenActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				sql="SELECT * FROM minesweeper WHERE id =(SELECT max(id) FROM minesweeper) ORDER BY time LIMIT 1";
				try {
			        conn = DriverManager.getConnection(url);
			        stmt = conn.createStatement();
			        String sqlusedb="use minesweeper_yl7544";
			        stmt.executeUpdate(sqlusedb);
			        
					ResultSet rs = stmt.executeQuery(sql);
					rs.next();
			        id=Integer.parseInt(rs.getString(1));
			        String f=rs.getString(2);
			        f=f.substring(1,f.length()-1);
			        String[] temp=f.split(", ");
			        for (int i=0;i<temp.length;i++) {
			        	field.set(i, Integer.parseInt(temp[i]));
			        	}
			        TimeRemain= Integer.parseInt(rs.getString(3));
			        MineRemain=Integer.parseInt(rs.getString(4));
			        Result=Integer.parseInt(rs.getString(5));
			        updateSet();
			       
			         conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        g.start(remain,time,field,Remain,1);
		        add(g,BorderLayout.CENTER);
			}
		}
	    ActionListener listener_o = new OpenActionListener();
	    item_o.addActionListener(listener_o);
	    
	    JMenuItem item_n = new JMenuItem("New");
		class NewActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				field=new ArrayList<Integer>();
				for(int i=0;i<GRID_ROW*GRID_COL;i++) {
					field.add(100);
				}
				TimeRemain=1000;
				MineRemain=40;
				Result=0;
				updateSet();
		        g.start(remain,time,field,Remain,0);
		        add(g,BorderLayout.CENTER);
		        id+=1;
			}
		}
	    ActionListener listener_n = new NewActionListener();
	    item_n.addActionListener(listener_n);
	    
	    JMenuItem item_s = new JMenuItem("Save");
		class SaveActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				updateGet();
				 String field_str = "\""+Arrays.toString(field.toArray())+"\""; 
				 sql="INSERT INTO minesweeper VALUE ("+id+","+field_str+","+TimeRemain+","+MineRemain+","+Result+")";
				 try {
				        conn = DriverManager.getConnection(url);
				        stmt = conn.createStatement();
				        String sqlusedb="use minesweeper_yl7544";
				        stmt.executeUpdate(sqlusedb);
					stmt.executeUpdate(sql);
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	    ActionListener listener_s = new SaveActionListener();
	    item_s.addActionListener(listener_s);
	    
	    
	    JMenuItem item_t = new JMenuItem("Top");
		class TopActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				sql="SELECT id,time FROM minesweeper where result=1 ORDER BY time DESC LIMIT 5";
				String top5="The top 5 are:\n";
				try {
			        conn = DriverManager.getConnection(url);
			        stmt = conn.createStatement();
			        String sqlusedb="use minesweeper_yl7544";
			        stmt.executeUpdate(sqlusedb);
			        
					ResultSet rs = stmt.executeQuery(sql);
					while(rs.next()) {
						top5+="id:"+rs.getString(1)+"    score:"+rs.getString(2)+"\n";
					
					}
					JOptionPane.showMessageDialog(null, top5);
					//System.out.println(top5);
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
	    ActionListener listener_t = new TopActionListener();
	    item_t.addActionListener(listener_t);
	    
	    menu.add(item_n);
	    menu.add(item_o);
	    menu.add(item_s);
	    menu.add(item_t);
	    menu.add(item_e);
	    menuBar.add(menu);

	}

	public static void main(String[] argv) {
		JFrame frame = new Minesweeper();
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}


}
