package socket_project_client;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Socket;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import lombok.Getter;

@Getter
public class ClientApp extends JFrame {

	private CardLayout mainCardLayout;

	private Socket socket;

	private JPanel mainCardPanel;
	private JPanel roomListPanel;

	private JTextField nickInputTextField;
	private JTextField roomMakeTxtField;
	private JTextField messageTextField;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientApp frame = new ClientApp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ClientApp() {
		mainCardLayout = new CardLayout();
		mainCardPanel = new JPanel();
		mainCardPanel.setLayout(mainCardLayout);
		setContentPane(mainCardPanel);

		JPanel loginPanel = new JPanel();
		mainCardPanel.add(loginPanel, "loginPanel");

		roomListPanel = new JPanel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 540);
		mainCardPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainCardPanel);
		mainCardPanel.add(roomListPanel, "roomListPanel");

		loginPanel.setLayout(null);

		JLabel welcomeLabel = new JLabel("WELCOME ! ");
		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		welcomeLabel.setBounds(12, 73, 400, 45);
		loginPanel.add(welcomeLabel);

		JLabel inputNickLabel = new JLabel("닉네임을 입력해주세요 !");
		inputNickLabel.setHorizontalAlignment(SwingConstants.CENTER);
		inputNickLabel.setBounds(12, 245, 400, 45);
		loginPanel.add(inputNickLabel);

		JLabel NickLabel = new JLabel("닉네임 : ");
		NickLabel.setHorizontalAlignment(SwingConstants.CENTER);
		NickLabel.setBounds(12, 160, 62, 45);
		loginPanel.add(NickLabel);

		nickInputTextField = new JTextField();
		nickInputTextField.addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				mainCardLayout.show(mainCardPanel, "roomListPanel");
			}
		}
		
		});
		
		
		
		nickInputTextField.setBounds(78, 160, 334, 45);
		loginPanel.add(nickInputTextField);
		nickInputTextField.setColumns(10);

		JButton confirmBtn = new JButton("입 장");

		confirmBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String nickname = nickInputTextField.getText();
				if (Objects.isNull(nickname)) {
					return;
				}
				if (nickname.isBlank()) {
					// 팝업창 추가지점(유정 07/12 17:44)
					return;
				}
				mainCardLayout.show(mainCardPanel, "roomListPanel");
			}
		});
		confirmBtn.setBounds(12, 333, 400, 50);
		loginPanel.add(confirmBtn);

		roomListPanel.setLayout(null);

		roomMakeTxtField = new JTextField();
		roomMakeTxtField.setBounds(12, 73, 308, 38);
		roomListPanel.add(roomMakeTxtField);
		roomMakeTxtField.setColumns(10);

		JScrollPane roomListScrollPane = new JScrollPane();
		roomListScrollPane.setBounds(12, 121, 402, 339);
		roomListPanel.add(roomListScrollPane);
		
		JPanel chatPanel = new JPanel();
		mainCardPanel.add(chatPanel, "chatPanel");
		chatPanel.setLayout(null);

		JList roomList = new JList();
		roomListScrollPane.setViewportView(roomList);

		JButton roomMakeBtn = new JButton("방 만들기");
		roomMakeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		roomMakeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainCardLayout.show(mainCardPanel, "chatPanel");
			}
		});
		roomMakeBtn.setBounds(326, 73, 88, 38);
		roomListPanel.add(roomMakeBtn);

		JLabel roomListTitle = new JLabel("<< 방 목 록 >>");
		roomListTitle.setHorizontalAlignment(SwingConstants.CENTER);
		roomListTitle.setBounds(12, 21, 402, 38);
		roomListPanel.add(roomListTitle);


		JLabel roomTitleLabel = new JLabel("방 제목");
		roomTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		roomTitleLabel.setBounds(12, 10, 273, 40);
		chatPanel.add(roomTitleLabel);

		JLabel myNameLabel = new JLabel("내이름");
		myNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		myNameLabel.setBounds(291, 60, 121, 32);
		chatPanel.add(myNameLabel);

		JButton exitBtn = new JButton("나가기 =>>");
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		exitBtn.setBounds(291, 10, 121, 40);
		chatPanel.add(exitBtn);

		JScrollPane messageAreaScrollPane = new JScrollPane();
		messageAreaScrollPane.setBounds(10, 71, 275, 367);
		chatPanel.add(messageAreaScrollPane);

		JTextArea messageArea = new JTextArea();
		messageAreaScrollPane.setViewportView(messageArea);

		JScrollPane userListScrollPane = new JScrollPane();
		userListScrollPane.setBounds(291, 94, 121, 344);
		chatPanel.add(userListScrollPane);

		JList userList = new JList();
		userListScrollPane.setViewportView(userList);

		messageTextField = new JTextField();
		messageTextField.setBounds(92, 448, 320, 33);
		chatPanel.add(messageTextField);
		messageTextField.setColumns(10);

		JLabel toLabel = new JLabel("to:");
		toLabel.setHorizontalAlignment(SwingConstants.CENTER);
		toLabel.setBounds(12, 448, 19, 33);
		chatPanel.add(toLabel);

		JLabel toUserLabel = new JLabel("All");
		toUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
		toUserLabel.setBounds(32, 448, 61, 33);
		chatPanel.add(toUserLabel);
	}
}
