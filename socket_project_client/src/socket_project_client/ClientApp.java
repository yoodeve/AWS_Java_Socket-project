package socket_project_client;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import lombok.Getter;
import socket_project_client.DTO.RequestBodyDTO;
import socket_project_client.DTO.SendMessage;

@Getter
public class ClientApp extends JFrame {
	private static ClientApp instance;

	public static ClientApp getInstance() {
		if (instance == null) {
			instance = new ClientApp();
		}
		return instance;

	}

	private String username;

	private Socket socket;

	private CardLayout mainCardLayout;
	private JPanel mainCardPanel;

	private static JTextField nickInputTextField;
	private JLabel myNameLabel;

	private JPanel roomListPanel;
	private JButton roomMakeBtn;
	private JScrollPane roomListScrollPane;
	private JTextField roomMakeTxtField;
	private JList roomList;
	private DefaultListModel<String> roomListModel;
	private JPanel loginPanel;

	private JPanel chatPanel;
	private DefaultListModel<String> userListModel;
	private JTextField messageTextField;
	private JTextArea messageArea;
	private JScrollPane messageAreaScrollPane;
	private JPanel chattingRoomPanel;
	private JList userList;

	private String selectedUser;

	private String firstPerson;

	private boolean isRoomOwner = false;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientApp frame = ClientApp.getInstance();
					frame.setVisible(true);

					CilentReceiver clientReceiver = new CilentReceiver();
					clientReceiver.start();

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

		nickInputTextField = new JTextField();
		nickInputTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					mainCardLayout.show(mainCardPanel, "roomListPanel");
				}
			}

		});

		chatPanel = new JPanel();
		mainCardPanel.add(chatPanel, "chatPanel");
		chatPanel.setLayout(null);

		nickInputTextField.setBounds(78, 160, 334, 45);
		loginPanel.add(nickInputTextField);
		nickInputTextField.setColumns(10);

		myNameLabel = new JLabel("내이름");
		myNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		myNameLabel.setBounds(291, 60, 121, 32);
		chatPanel.add(myNameLabel);

		try {
			socket = new Socket("127.0.0.1", 8000);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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

		JLabel nickLabel = new JLabel("닉네임 : ");
		nickLabel.setHorizontalAlignment(SwingConstants.CENTER);
		nickLabel.setBounds(12, 160, 62, 45);
		loginPanel.add(nickLabel);

		JButton confirmBtn = new JButton("입 장");

		confirmBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
// 닉네임버튼 클릭
				String nickname = nickInputTextField.getText();
				if (Objects.isNull(nickname)) {
					return;
				}
				if (nickname.isBlank()) {
// 팝업창 추가지점(유정 07/12 17:44)

					return;
				}

				if (!isRoomOwner) {
					myNameLabel.setText("★방장★" + nickname);
					isRoomOwner = false;
				} else {
					myNameLabel.setText(nickname);
				}

				RequestBodyDTO<String> requestBodyDto = new RequestBodyDTO<String>("connection", nickname);
				System.out.println("frame.username" + nickname);
				ClientSender.getInstance().send(requestBodyDto);

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

		roomListScrollPane = new JScrollPane();
		roomListScrollPane.setBounds(12, 121, 402, 339);
		roomListPanel.add(roomListScrollPane);

		JLabel toLabel = new JLabel("to:");
		toLabel.setHorizontalAlignment(SwingConstants.CENTER);
		toLabel.setBounds(12, 448, 19, 33);
		chatPanel.add(toLabel);

		JLabel toUserLabel = new JLabel("All");
		toUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
		toUserLabel.setBounds(32, 448, 61, 33);
		chatPanel.add(toUserLabel);

		JScrollPane userListScrollPane = new JScrollPane();
		userListScrollPane.setBounds(291, 94, 121, 344);
		chatPanel.add(userListScrollPane);

		roomMakeBtn = new JButton("방 만들기");
		roomMakeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		roomMakeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
// 방만들기 버튼 클릭
				String roomName = roomMakeTxtField.getText();
				if (Objects.isNull(roomName)) {
					return;
				}
				if (roomName.isBlank()) {
					JOptionPane.showMessageDialog(roomListPanel, "방제목을 입력하세요.", "방만들기 실패", JOptionPane.ERROR_MESSAGE);
					return;
				}

				for (int i = 0; i < roomListModel.size(); i++) {
					if (roomListModel.get(i).equals(roomName)) {
						JOptionPane.showMessageDialog(roomListPanel, "이미 존재하는 방제목입니다.", "방만들기 실패",
								JOptionPane.ERROR_MESSAGE);
						return;

					}

				}
				roomListModel.addElement(roomName);
				RequestBodyDTO<String> requestBodyDTO = new RequestBodyDTO<String>("createRoom", roomName);
				ClientSender.getInstance().send(requestBodyDTO);
				roomMakeTxtField.setText("");
			}
		});

		roomListModel = new DefaultListModel<String>();
		roomList = new JList(roomListModel);
		roomListScrollPane.setViewportView(roomList);

		JLabel roomListTitle = new JLabel("<< 방 목 록 >>");
		roomListTitle.setHorizontalAlignment(SwingConstants.CENTER);
		roomListTitle.setBounds(12, 21, 402, 38);
		roomListPanel.add(roomListTitle);

		JLabel roomTitleLabel = new JLabel("방 제목");
		roomTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		roomTitleLabel.setBounds(12, 10, 273, 40);
		chatPanel.add(roomTitleLabel);

		roomList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
// 룸리스트 클릭(더블클릭) => 채팅룸 입장
				if (e.getClickCount() == 2) {
					String roomName = roomListModel.get(roomList.getSelectedIndex());
					mainCardLayout.show(mainCardPanel, "chatPanel");
					roomTitleLabel.setText(roomName);

					RequestBodyDTO<String> requestOwnerNameDto = new RequestBodyDTO<String>("enter", roomName);
					ClientSender.getInstance().send(requestOwnerNameDto);
				}
			}
		});

		roomMakeBtn.setBounds(326, 73, 88, 38);
		roomListPanel.add(roomMakeBtn);

		JButton exitBtn = new JButton("나가기 =>>");
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String roomName = roomListModel.get(roomList.getSelectedIndex());
				mainCardLayout.show(mainCardPanel, "roomListPanel");
				RequestBodyDTO<String> requestBodyDTO = new RequestBodyDTO<String>("exitRoom", roomName);
				ClientSender.getInstance().send(requestBodyDTO);

			}
		});

		exitBtn.setBounds(291, 10, 121, 40);
		chatPanel.add(exitBtn);

		chattingRoomPanel = new JPanel();
		chattingRoomPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		chattingRoomPanel.setLayout(null);
		mainCardPanel.add(chattingRoomPanel, "chattingRoomPanel");

		messageAreaScrollPane = new JScrollPane();
		messageAreaScrollPane.setBounds(10, 71, 275, 367);
		chatPanel.add(messageAreaScrollPane);

		messageArea = new JTextArea();
		messageAreaScrollPane.setViewportView(messageArea);
		messageArea.setEditable(false);

		messageTextField = new JTextField();
		messageTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// 메세지 전송
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String messageText = messageTextField.getText();

					if (!messageText.isBlank()) {
						if (selectedUser == null || selectedUser.equals("All")) {
							SendMessage sendMessage = SendMessage.builder().fromUsername(nickInputTextField.getText())
									.messageBody(messageText).build();
							RequestBodyDTO<SendMessage> requestBodyDTO = new RequestBodyDTO<SendMessage>("sendMessage",
									sendMessage);
							ClientSender.getInstance().send(requestBodyDTO);
							messageTextField.setText("");
						}
						// 모든 유저에게 메시지 보내기

						else {// 선택한 유저에게 귓속말 보내기
							SendMessage sendMessage = SendMessage.builder().fromUsername(nickInputTextField.getText())
									.toUsername(selectedUser).messageBody(messageText).build();

							RequestBodyDTO<SendMessage> requestBodyDTO = new RequestBodyDTO<>("sendPrivateMessage",
									sendMessage);
							ClientSender.getInstance().send(requestBodyDTO);
							selectedUser = "All";
							messageTextField.setText("");
							toUserLabel.setText("All");
						}
					}
				}
			}
		});

		messageTextField.setBounds(92, 448, 320, 33);
		chatPanel.add(messageTextField);
		messageTextField.setColumns(10);

		userListModel = new DefaultListModel<>();
		userList = new JList(userListModel);
		userListScrollPane.setViewportView(userList);

		userList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 유저 목록 더블 클릭
				if (e.getClickCount() == 2) {
					selectedUser = (String) userList.getSelectedValue();
					if (!selectedUser.equals(myNameLabel.getText())) {
						toUserLabel.setText(selectedUser);
					}

				}
			}

		});
	}
}