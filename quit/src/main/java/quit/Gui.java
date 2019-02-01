package quit;

import javax.swing.JFrame;
import javax.swing.JLabel;

import quit.SimplePaint.SimplePaintPanel;

public class Gui extends JFrame {

	private static final long serialVersionUID = -7059851866000922177L;

	private JLabel imageHolder;

	private SimplePaintPanel paintPanel;
	
	public Gui() {
		super();
		initialize();
	}

	private void initialize() {
		setBounds(100, 100, 1256, 550);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		setTitle("QUIT - Quadro Unificado Interativo");
		setLocationRelativeTo(null);
		
		imageHolder = new JLabel();
		imageHolder.setBounds(0, 0, 640, 480);
		getContentPane().add(imageHolder);
		
		paintPanel = new SimplePaintPanel();
		paintPanel.setBounds(640, 0, 600, 480);
		getContentPane().add(paintPanel);

	}

	public JLabel getImageHolder() {
		return imageHolder;
	}

	public void setImageHolder(JLabel imageHolder) {
		this.imageHolder = imageHolder;
	}
}
