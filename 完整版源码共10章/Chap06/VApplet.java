

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Component;
import java.util.Vector; // JMF��ص���
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.format.VideoFormat;
import javax.swing.JPanel;
import javax.swing.JApplet;

public class VApplet extends JApplet {
	private JPanel jContentPane = null;
	private Choice choice = null;

	public VApplet() {
		super();
	}

	public void init() {
		this.setSize(320, 240);
		this.setContentPane(getJContentPane());
		this.setName("VApplet");
	}

	// ȡϵͳ���пɲɼ���Ӳ���豸�б�
	private CaptureDeviceInfo[] getDevices() {
		Vector devices = CaptureDeviceManager.getDeviceList(null);
		CaptureDeviceInfo[] info = new CaptureDeviceInfo[devices.size()];
		for (int i = 0; i < devices.size(); i++) {
			info[i] = (CaptureDeviceInfo) devices.get(i);
		}
		return info;
	}

	// ����֪�豸��ȡ������Ƶ�豸���б�
	private CaptureDeviceInfo[] getVideoDevices() {
		CaptureDeviceInfo[] info = getDevices();
		CaptureDeviceInfo[] videoDevInfo;
		Vector vc = new Vector();
		for (int i = 0; i < info.length; i++) {
			// ȡ�豸֧�ֵĸ�ʽ�������һ������Ƶ��ʽ������Ϊ���豸Ϊ��Ƶ�豸
			Format[] fmt = info[i].getFormats();
			for (int j = 0; j < fmt.length; j++) {
				if (fmt[j] instanceof VideoFormat) {
					vc.add(info[i]);
				}
				break;
			}
		}
		videoDevInfo = new CaptureDeviceInfo[vc.size()];
		for (int i = 0; i < vc.size(); i++) {
			videoDevInfo[i] = (CaptureDeviceInfo) vc.get(i);
		}
		return videoDevInfo;
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			BorderLayout borderLayout = new BorderLayout();
			jContentPane = new JPanel();
			jContentPane.setLayout(borderLayout);

			MediaLocator ml = null;
			Player player = null;
			try {
				// ������ֻ��һ����Ƶ�豸��ֱ��ȡ��һ��
				// ȡ�õ�ǰ�豸��MediaLocator
				ml = getVideoDevices()[0].getLocator();
				// ���Ѿ�ȡ�õ�MediaLocator�õ�һ��Player
				player = Manager.createRealizedPlayer(ml);
				player.start();
				// ȡ��Player��AWT Component
				Component comp = player.getVisualComponent();
				// �������Ƶ�豸�������������null,����Ҫ���ж�һ��
				if (comp != null) {
					// ��Component�ӵ�����
					jContentPane.add(comp, BorderLayout.EAST);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jContentPane;
	}
}
