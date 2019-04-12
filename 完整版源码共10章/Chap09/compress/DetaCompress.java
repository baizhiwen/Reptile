package com.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DetaCompress {

	public static byte[] longToBytes(long n) {
		byte[] buf = new byte[8];// �½�һ��byte����
		for (int i = buf.length - 1; i >= 0; i--) {
			buf[i] = (byte) (n & 0x00000000000000ff);// ȡ��8λ��ֵ
			n >>>= 8;// ����8λ
		}
		return buf;
	}

	// ��һ��long�͵����ݽ���ѹ��
	public static void writeVLong(long i, BufferedOutputStream dos)
			throws IOException {
		while ((i & ~0x7F) != 0) {
			dos.write((byte) ((i & 0x7f) | 0x80)); // д���λ�ֽ�
			i >>>= 7; // ����7λ
		}

		dos.write((byte) i);
		// System.out.println((byte)i+"    д���λ�ֽ�");

	}

	// ��һ��ѹ�����long�͵����ݶ�ȡ����
	static long readVLong(DataInputStream dis) throws IOException {
		byte b = dis.readByte(); // ����һ���ֽ�
		int i = b & 0x7F; // ȡ��7λ��ֵ
		// ÿ����λ���ֽڶ�˸�2��7�η���Ҳ����128
		for (int shift = 7; (b & 0x80) != 0; shift += 7) {
			if (dis.available() != 0) {
				b = dis.readByte();
				i |= (b & 0x7F) << shift; // ��ǰ�ֽڱ�ʾ��λ��2��shift�η�
			}
		}
		return i;// �������ս��i
	}

	// ��long������simHashSetд��fileNameָ�����ļ���ȥ
	static int write(long[] simHashSet, String fileName) {
		int j = 0;
		try {
			BufferedOutputStream dos = new BufferedOutputStream(
					new FileOutputStream(fileName));
			byte[] b = longToBytes(simHashSet[0]);// ����ĵ�һ������һ��ת���ɶ�����
			dos.write(b);// ����д���ļ���
			for (int i = 1; i < simHashSet.length; i++) {
				long lo = simHashSet[i] - simHashSet[i - 1];// ��һ��������¼�����к�һ������ǰһ�����Ĳ�
				writeVLong(lo, dos);// �������ֵд���ļ�
			}
			dos.close();
			j = simHashSet.length;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return j;
	}

	// ��fileNameָ�����ļ��а�long������д����
	static long[] read(int len, String fileName) {
		try {
			DataInputStream dis = new DataInputStream(new BufferedInputStream(
					new FileInputStream(fileName)));
			long[] simHashSet = new long[len];
			simHashSet[0] = dis.readLong();// ���ļ���ȡ��һ��long�����ַ�������
			for (int i = 1; i < len; i++) {
				simHashSet[i] = readVLong(dis);// ��ȡ�ļ�ʣ�µ�Ԫ��
				simHashSet[i] = simHashSet[i] + simHashSet[i - 1];  // ��Ԫ�ض���������һ������ǰһ�����ֵĺ�
			}
			dis.close();
			
			return simHashSet;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
