package com.lietu.classify;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClassifierParam {
	//ѵ��ʱ��Ҫʹ�õĲ���
	public	String m_txtTrainDir;
	public	String m_txtResultDir;
	public	int		m_nFSMode;        //������������
	public	int     m_nWordSize;      //������Ŀ
	public	int     m_nSelMode;       //ȫ�ִ�ֻ��ǰ�����ּ������������ֶ�
	public	int     m_nOpMode;        //������ʵķ�ʽ
	public	int     m_nLanguageType;  //�ĵ�������
	public	boolean    m_bStem;          //�Ƿ���дʸɳ�ȡ
	public	int     m_nWeightMode;    //������Ȩ�ķ���
	
	//����ʱ��Ҫʹ�õĲ���
	public  int m_nClassifyType;              //0  �������; 1 �������
	public  boolean m_bEvaluation;               //�Ƿ���Ҫ�Բ��Խ����������
	public  boolean m_bCopyFiles;                //�Ƿ񽫷ֺ�����ļ����������Ŀ¼��
	public  String m_strTestDir;             //�����ĵ��������ڵ�Ŀ¼
	public  String m_strResultDir;           //���Խ�����ڵ�Ŀ¼
	public  String m_strModelFile;
	public  int m_nKNN;                       //KNN�㷨��kֵ
	public  int m_nDocFormat;                 //�����ĵ��ĸ�ʽ
	public  double m_dThreshold;              //�������ʱʹ�õ���ֵ
	public  int m_nClassifierType;            //������������: -1 δ֪, 0 ����KNN, 1 ����SVM
	
	public String toString()
	{
		StringBuilder strParam = new StringBuilder();
		
		strParam.setLength(0);
		strParam.append("ѵ���ĵ�Ŀ¼:\t\t");
		strParam.append(m_txtTrainDir);
		strParam.append("\r\n");
		
		strParam.append("ѵ�����Ŀ¼:\t\t");
		strParam.append(m_txtResultDir);
		strParam.append("\r\n");
		
		strParam.append("���ʹ��㷽��:\t\t");
		if(m_nOpMode==ClassifierParam.nOpDocMode)
			strParam.append("�����ĵ�ͳ��\r\n");
		else if(m_nOpMode==ClassifierParam.nOpWordMode)
			strParam.append("���ڴ�Ƶͳ��\r\n");
		else
			strParam.append("δ֪\r\n");
		
		strParam.append("����ѡ�񷽷�:\t\t");
		if(m_nFSMode==ClassifierParam.nFS_IGMode)
			strParam.append("��Ϣ����\r\n");
		else if(m_nFSMode==ClassifierParam.nFS_MIMode)
			strParam.append("����Ϣ\r\n");
		else if(m_nFSMode==ClassifierParam.nFS_CEMode)
			strParam.append("����������\r\n");
		else if(m_nFSMode==ClassifierParam.nFS_X2Mode)
			strParam.append("X^2ͳ��\r\n");
		else if(m_nFSMode==ClassifierParam.nFS_WEMode)
			strParam.append("�ı�֤��Ȩ��\r\n");
		else if(m_nFSMode==ClassifierParam.nFS_XXMode)
			strParam.append("�Ұ���Ϣ����\r\n");
		else
			strParam.append("δ֪\r\n");

		strParam.append("����ѡ��ʽ:\t\t");
		if(m_nSelMode==ClassifierParam.nFSM_GolbalMode)
			strParam.append("ȫ��ѡȡ\r\n");
		else if(m_nSelMode==ClassifierParam.nFSM_IndividualModel)
			strParam.append("����𵥶�ѡȡ\r\n");
		else
			strParam.append("δ֪\r\n");

		strParam.append("�ĵ���������:\t\t");
		if(m_nLanguageType==ClassifierParam.nLT_Chinese)
			strParam.append("����\r\n");
		else if(m_nLanguageType==ClassifierParam.nLT_English)
		{
			strParam.append("Ӣ��\r\n");
			if(m_bStem)
				strParam.append("�Ƿ�ʸɳ�ȡ:\t\t��\r\n");
			else
				strParam.append("�Ƿ�ʸɳ�ȡ:\t\t��\r\n");
		}
		else
			strParam.append("δ֪\r\n");

		strParam.append("������Ȩ�㷨:\t\t");
		if(m_nWeightMode==ClassifierParam.nWM_TF_IDF)
			strParam.append("TF*IDF\r\n");
		else if(m_nWeightMode==ClassifierParam.nWM_TF_DIFF)
			strParam.append("TF*������������ֵ\r\n");
		else if(m_nWeightMode==ClassifierParam.nWM_TF_IDF_DIFF)
			strParam.append("TF*IDF*������������ֵ\r\n");
		else
			strParam.append("δ֪\r\n");

		String strWordSize;
		strWordSize=String.format("�����ռ�ά��:\t\t%d\r\n",m_nWordSize);
		strParam.append(strWordSize);

		//CString nstrWordSize;
		//int			nDistinctWordNum = theClassifier.m_lstWordList.GetCount();
		//nstrWordSize.Format("ѵ���ĵ����в��ظ��ĵ�������:\t\t%d\r\n",nDistinctWordNum);
		//strParam+=nstrWordSize;

		if(m_nClassifierType==ClassifierParam.nCT_KNN)
			strParam.append("����������: \t\tKNN\r\n");
		else if(m_nClassifierType==ClassifierParam.nCT_SVM)
			strParam.append("����������: \t\tSVM\r\n");
		else
			strParam.append("���ȴ�һ������ģ���ļ�!");
		
		return strParam.toString();
	}
	
	// calculation model
	public static final int nOpDocMode = 0;      // based on document number model
	public static final int nOpWordMode = 1;     // based on word number model
	
	// feature evaluation fuction
	public static final int nFS_IGMode  = 0;      // Information gain feature selection
	public static final int nFS_MIMode  = 1;      // Mutual Informaiton feature selection
	public static final int nFS_CEMode  = 2;      // Cross Entropy for text feature selection
	public static final int nFS_X2Mode  = 3;      // X^2 Statistics feature selection
	public static final int nFS_WEMode  = 4;      // Weight of Evielence for text feature selection
	public static final int nFS_XXMode  = 5;      // Right half of IG
	
	// how to select features
	public static final int nFSM_GolbalMode=0;  // ȫ��ѡ
	public static final int nFSM_IndividualModel=1; // ����ѡ

	// classifier type
	public static final int nCT_Unknown=-1;     // Unknown
	public static final int nCT_KNN=0;         // KNN
	public static final int nCT_SVM=1;         // SVM

	// document language type
	public static final int nLT_Chinese=0;     // Chinese
	public static final int nLT_English=1;     // English

	// document format
	public static final int nDF_Directory=0;   // Directory
	public static final int nDF_Smart=1;       // Smart

	// classify type
	public static final int nFT_Single=0;      // Single Classification
	public static final int nFT_Multi=1;       // Multiple Classification

	// weight mode
	public static final int nWM_TF_IDF=0;      // TF*IDF
	public static final int nWM_TF_DIFF=1;     // TF*DIFF
	public static final int nWM_TF_IDF_DIFF=2; // TF*IDF*DIFF
	
	public ClassifierParam(){
		//ѵ��ʱ��Ҫʹ�õĲ���
		m_txtTrainDir = "";
		m_txtResultDir = "";
		m_nFSMode = ClassifierParam.nFS_X2Mode;//ClassifierParam.nFS_IGMode;
		m_nWordSize = 5000;
		m_nSelMode= ClassifierParam.nFSM_GolbalMode;
		m_nOpMode= ClassifierParam.nOpWordMode;//ClassifierParam.nOpDocMode;
		m_nLanguageType= ClassifierParam.nLT_Chinese;
		m_bStem=false;
		m_nWeightMode=0;
		//����ʱ��Ҫʹ�õĲ���
		m_nClassifyType=0;//�������
		m_bEvaluation=true;
		m_bCopyFiles=false;
		m_strTestDir="";
		m_strResultDir="";
		m_strModelFile="model";
		m_nDocFormat=ClassifierParam.nDF_Directory;
		m_nKNN=35;
		m_dThreshold=60;
		m_nClassifierType=ClassifierParam.nCT_SVM;
	}
	
	public void dumpToFile(String strFileName){
		LEDataOutputStream fBinOut = null;
		try {
			fBinOut = new LEDataOutputStream(new FileOutputStream(
					strFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			fBinOut.writeGBKString(m_txtTrainDir);
			fBinOut.writeGBKString(m_txtResultDir);
			fBinOut.writeInt(m_nFSMode);
			fBinOut.writeInt(m_nWordSize);
			fBinOut.writeInt(m_nSelMode);
			fBinOut.writeInt(m_nOpMode);
			fBinOut.writeInt(m_nLanguageType);
			fBinOut.writeBoolean(m_bStem);
			fBinOut.writeInt(m_nWeightMode);
			fBinOut.writeInt(m_nClassifyType);
			fBinOut.writeBoolean(m_bEvaluation);
			fBinOut.writeBoolean(m_bCopyFiles);
			fBinOut.writeGBKString(m_strTestDir);
			fBinOut.writeGBKString(m_strResultDir);
			fBinOut.writeGBKString(m_strModelFile);
			fBinOut.writeInt(m_nDocFormat);
			fBinOut.writeInt(m_nKNN);
			fBinOut.writeDouble(m_dThreshold);
			fBinOut.writeInt(m_nClassifierType);
			
			fBinOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean getFromFile(String strFileName)
	{
		//System.out.println("ClassifierParam.file:"+strFileName);
		
		LEDataInputStream fBinIn = null;
		
		try
		{
			fBinIn = new LEDataInputStream(new FileInputStream(
				strFileName));
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("�޷����ļ�"+strFileName+"!") ;
			return false;
		}

		try
		{
			//ѵ��ʱ��Ҫʹ�õĲ���
			m_txtTrainDir= fBinIn.readGBKString();			
			m_txtResultDir= fBinIn.readGBKString();			
			m_nFSMode = fBinIn.readInt();
			m_nWordSize = fBinIn.readInt();
			m_nSelMode = fBinIn.readInt();
			m_nOpMode = fBinIn.readInt();
			m_nLanguageType = fBinIn.readInt();
			m_bStem = fBinIn.readBoolean();
			m_nWeightMode = fBinIn.readInt();
			//����ʱ��Ҫʹ�õĲ���
			m_nClassifyType = fBinIn.readInt();
			m_bEvaluation = fBinIn.readBoolean();
			m_bCopyFiles = fBinIn.readBoolean();
			m_strTestDir= fBinIn.readGBKString();
			m_strResultDir= fBinIn.readGBKString();

			m_strModelFile= fBinIn.readGBKString();
			m_nDocFormat = fBinIn.readInt();
			m_nKNN = fBinIn.readInt();
			m_dThreshold= fBinIn.readDouble();
			m_nClassifierType = fBinIn.readInt();
			
			fBinIn.close();
		}
		catch(Exception e)
		{
			System.out.println("�޷����ļ�"+strFileName+"!") ;
			return false;
		}
		return true;
	}
}
