package com.lietu.classify;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Map.Entry;

import com.lietu.svmLight.Model;
import com.lietu.svmLight.SVM;

public class Trainer {

	public static class SortType implements Comparable<SortType> {
		String word;
		double dWeight;
		WordNode pclsWordNode;
		
		public String toString()
		{
			return "word:"+word+ " dWeight:"+dWeight;
		}

		@Override
		public int compareTo(SortType o) {
			if (this.dWeight > o.dWeight)
				return -1;
			else if (this.dWeight < o.dWeight)
				return 1;
			return 0;
		}
	}

	public ClassifierParam m_paramClassifier = new ClassifierParam();
	
	public CatalogList m_lstTrainCatalogList = new CatalogList();
	public CatalogList m_lstTestCatalogList;
	public SVM docSVM = new SVM();
	Model[] models;

	// ����ģ���ļ�ͷ��ʶ��
	private static int dwModelFileID = 0xFFEFFFFF;

	public Trainer() {
	}

	public void setTrainSet(String path) {
		m_paramClassifier.m_txtTrainDir = path;
	}

	public void setModel(String path) {
		m_paramClassifier.m_txtResultDir = path;
	}

	// ����bGenDic=false������������ɨ���ĵ��õ�ѵ���ĵ�������������,һ���ڲ�η���ʱʹ��
	// ����nType������������ģ�͵����,nType=0����KNN������,nType=1����SVM������
	public boolean train(int featherSelectionAlgorithmn) throws Exception {
		long startTime = System.currentTimeMillis();
		long totalTime;
		
		// �������к�ѡ��������䱣����m_lstWordList��
		WordList m_lstWordList = genDic();

		if (m_lstWordList == null)
			return false;
		int distinctWordNum = m_lstWordList.getCount();
		if (distinctWordNum == 0)
			return false;
		
		System.out.println("ѵ���ĵ����в��ظ��ĵ�������:" + distinctWordNum);
		if (m_lstTrainCatalogList.getCataNum() == 0)
			return false;

		// Ϊ�������б�m_lstWordList�е�ÿ��������Ȩ
		System.out.println("��ʼ�����ѡ��������ÿ��������������ֶȣ����Ժ�...");
		startTime = System.currentTimeMillis();
		featherWeight(m_lstWordList, featherSelectionAlgorithmn);
		totalTime = System.currentTimeMillis() - startTime;
		System.out.println("�������ֶȼ����������ʱ" + totalTime / 1000 + "��");

		
		System.out.println("��ʼ��������ѡ�����Ժ�...");
		startTime = System.currentTimeMillis();

		// ���������б�m_lstWordList��ѡ����������
		WordList m_lstTrainWordList = featherSelection(m_lstWordList);
		if(m_lstTrainWordList == null)
		{
			throw new Exception("error WordList cannot be null");
		}
		
		m_lstTrainWordList.indexWord();
		totalTime = System.currentTimeMillis() - startTime;
		System.out.println("����ѡ���������ʱ" + totalTime / 1000 + "��");

		System.out.println("��ʼ�����ĵ����������Ժ�...");
		startTime = System.currentTimeMillis();
		genModel(m_lstTrainWordList);
		totalTime = System.currentTimeMillis() - startTime;
		System.out.println("�ĵ��������ɽ�������ʱ" + totalTime / 1000 + "��");

		System.out.println("��ʼ�������ģ�ͣ����Ժ�...");
		startTime = System.currentTimeMillis();
		File modelDir = new File(m_paramClassifier.m_txtResultDir);
		if (!modelDir.exists()) {
			modelDir.mkdir();
		}
		WriteModel(m_paramClassifier.m_txtResultDir + "\\model.prj",m_lstTrainWordList);
		totalTime = System.currentTimeMillis() - startTime;
		System.out.println("�������ģ�ͽ�������ʱ" + totalTime / 1000 + "��");

		// ѵ��SVM�����������ڱ���ѵ���ĵ����ĵ����������
		System.out.println("��ʼѵ��SVM�����Ժ�...");
		m_lstTrainCatalogList.initCatalogList2(); // ɾ���ĵ�������ռ�õĿռ�
		startTime = System.currentTimeMillis();
		trainSVM();
		totalTime = System.currentTimeMillis() - startTime;
		System.out.println("SVM������ѵ����������ʱ" + totalTime / 1000 + "��");

		// Ϊ��������׼��,�����ܽ��з���
		// Prepare();
		return true;
	}

	// generate original dictionary (the largest one)
	// form train files
	public WordList genDic() {
		// ѵ��ʱ��Ҫ�õ���,����������û�н�������ѡ��֮ǰѵ���������е�����
		WordList m_lstWordList = new WordList();
		
		long startTime;
		long totalTime;

		startTime = System.currentTimeMillis();
		System.out.println("��ʼɨ��ѵ���ĵ������Ժ�...");
		if (m_lstTrainCatalogList.buildLib(m_paramClassifier.m_txtTrainDir) <= 0) {
			System.out.println("ѵ���ĵ�������Ϊ0!");
			return null;
		}

		int nCount;
		int nCataNum = m_lstTrainCatalogList.getCataNum();
		for (CatalogNode catalogNode : m_lstTrainCatalogList.catalogList) {
			for (DocNode docnode : catalogNode.docList) {
				if (m_paramClassifier.m_nLanguageType == ClassifierParam.nLT_Chinese) {
					// System.out.println("ɨ�������ĵ�"+docnode.m_strDocName);

					nCount = docnode.scanChinese(catalogNode.m_strDirName,
							m_lstWordList, nCataNum, catalogNode.m_idxCata);
				} else {
					nCount = docnode.scanEnglish(catalogNode.m_strDirName,
							m_lstWordList, nCataNum, catalogNode.m_idxCata,
							m_paramClassifier.m_bStem);
				}
				if (nCount == 0) {
					// System.out.println("�ļ�"+catalognode.m_strDirName+"/"+
					// docnode.m_strDocName+"������!");
					continue;
				} else if (nCount < 0) {
					System.out.println("�ļ�" + catalogNode.m_strDirName + "/"
							+ docnode.m_strDocName + "�޷���!");
					continue;
				}
				catalogNode.m_lTotalWordNum += nCount;// information collection
														// point
			}
		}

		totalTime = System.currentTimeMillis() - startTime;
		System.out.println("ɨ��ѵ���ĵ���������ʱ" + totalTime / 1000 + "��");
		return m_lstWordList;
	}

	public void trainSVM() {
		long tmStart;
		long tmSpan;

		m_paramClassifier.m_strModelFile = "model";
		for (int i = 1; i <= m_lstTrainCatalogList.getCataNum(); i++) {
			tmStart = System.currentTimeMillis();
			System.out.println("����ѵ����" + i + "��SVM�����������Ժ�...");
			docSVM.param.trainfile = m_paramClassifier.m_txtResultDir
					+ "/train.txt";
			docSVM.param.modelfile = (m_paramClassifier.m_txtResultDir + "/"
					+ m_paramClassifier.m_strModelFile + i + ".mdl");
			docSVM.svm_learn_main(i);
			tmSpan = System.currentTimeMillis() - tmStart;
			System.out.println("��" + i + "��SVM������ѵ����ɣ���ʱ" + tmSpan + "!");
		}
	}

	// Give m_lstWordList & m_lstTrainCatalogList
	// Output the present vector of each document;
	// bFlag=false ��η����ʱ��ʹ��
	public void genModel(WordList m_lstTrainWordList) {
		int m_nAllocTempLen = m_lstTrainWordList.getCount();
		
		//System.out.println("AllocTempBuffer:"+nLen);
		WeightNode[] m_pTemp=new WeightNode[m_nAllocTempLen];
		
		// for each catalog
		for (CatalogNode cataNode : m_lstTrainCatalogList.catalogList) 
		{
			// ȡ���б��е�ÿһ����
			// CCatalogNode& cataNode = m_lstTrainCatalogList.GetNext(pos_cata);
			// POSITION pos_doc = cataNode.GetFirstPosition();
			for (DocNode docNode : cataNode.docList) {
				if (m_paramClassifier.m_nLanguageType == ClassifierParam.nLT_Chinese)
				{
					docNode.scanChineseWithDict(cataNode.m_strDirName,
							m_lstTrainWordList,m_pTemp);
				}
				else
				{
					docNode.scanEnglishWithDict(cataNode.m_strDirName,
							m_lstTrainWordList, m_paramClassifier.m_bStem,
							m_pTemp);
				}
				docNode.genDocVector(m_pTemp);
				// System.out.println("�����ĵ�"+docNode.m_strDocName+"���ĵ�����");
			}
		}
	}

	public void featherWeight(WordList wordList, int featherSelectionMethod) {
		
		m_paramClassifier.m_nFSMode = featherSelectionMethod;
		//----------------------------------------------------------------------
		// --------
		// based on document number model
		long N; // �ܵ��ĵ���;
		int N_c; // C����ĵ���
		long N_ft; // ����ft���ĵ���
		long N_c_ft; // C���к���ft���ĵ���
		//----------------------------------------------------------------------
		// --------
		// based on word number model
		long N_W; // �ܵĴ��� m_lWordNum;
		long N_W_C; // C����� CCatalogNode.m_lTotalWordNum;
		long N_W_f_t; // f_t���ֵ��ܴ���
		long N_W_C_f_t;// C����f_t���ֵĴ���
		//----------------------------------------------------------------------
		// --------
		double P_c_ft, P_c_n_ft, P_n_c_ft, P_n_c_n_ft;
		double P_c, P_n_c;
		double P_ft, P_n_ft;

		// calculate the weight of each word to all catalog
		N = m_lstTrainCatalogList.getDocNum(); //�ĵ�����
		N_W = wordList.getWordNum(); // �ܵĴ���


		for (Entry<String, WordNode> e : wordList.m_lstWordList.entrySet()) // for
																			// each
																			// word
		{
			WordNode wordNode = e.getValue();
			wordNode.m_dWeight = 0;

			N_ft = wordNode.getDocNum();//�ĵ�����
			//System.out.println("����:"+e.getKey()+"...N_ft "+N_ft);
			N_W_f_t = wordNode.getWordNum();//������
			int nCataCount = 0;
			for (CatalogNode cataNode : m_lstTrainCatalogList.catalogList)
			{
				N_c = cataNode.getDocNum();
				//System.out.println("����:"+e.getKey()+"...N_c "+N_c);
				N_W_C = cataNode.m_lTotalWordNum;
				N_c_ft = wordNode.getCataDocNum(cataNode.m_idxCata);
				N_W_C_f_t = wordNode.getCataWordNum(cataNode.m_idxCata);
				// calculation model ���������ʵķ�ʽ�ǻ��ڴ�Ƶͳ��
				if (m_paramClassifier.m_nOpMode == ClassifierParam.nOpWordMode) {
					P_c = 1.0 * N_W_C / N_W;
					P_ft = 1.0 * N_W_f_t / N_W;
					P_c_ft = 1.0 * N_W_C_f_t / N_W;
				} else // if(m_paramClassifier.m_nOpMode==CClassifierParam::
						// nOpDocMode)���������ʵķ�ʽ�ǻ����ĵ���ͳ��
				{
					P_c = 1.0 * N_c / N;
					P_ft = 1.0 * N_ft / N;
					P_c_ft = 1.0 * N_c_ft / N;
				}

				P_n_c = 1 - P_c;
				P_n_ft = 1 - P_ft;
				P_n_c_ft = P_ft - P_c_ft;
				P_c_n_ft = P_c - P_c_ft;
				P_n_c_n_ft = P_n_ft - P_c_n_ft;

				wordNode.catWeight[nCataCount] = 0;
				// feature selection model
				if (m_paramClassifier.m_nFSMode == ClassifierParam.nFS_XXMode) {
					// Right half of IG
					if ((Math.abs(P_c * P_n_ft) > Double.MIN_VALUE)
							&& (Math.abs(P_c_n_ft) > Double.MIN_VALUE)) {
						wordNode.catWeight[nCataCount] += P_c_n_ft
								* Math.log(P_c_n_ft / (P_c * P_n_ft));
					}
				} else if (m_paramClassifier.m_nFSMode == ClassifierParam.nFS_MIMode) {
					// Mutual Informaiton feature selection
					if ((Math.abs(P_c * P_ft) > Double.MIN_VALUE)
							&& (Math.abs(P_c_ft) > Double.MIN_VALUE)) {
						wordNode.catWeight[nCataCount] += P_c
								* Math.log(P_c_ft / (P_c * P_ft));
					}
				} else if (m_paramClassifier.m_nFSMode == ClassifierParam.nFS_CEMode) {
					// Cross Entropy for text feature selection
					if ((Math.abs(P_c * P_ft) > Double.MIN_VALUE)
							&& (Math.abs(P_c_ft) > Double.MIN_VALUE)) {
						wordNode.catWeight[nCataCount] += P_c_ft
								* Math.log(P_c_ft / (P_c * P_ft));
					}
				} else if (m_paramClassifier.m_nFSMode == ClassifierParam.nFS_X2Mode) {
					// X^2 Statistics feature selection
					if ((Math.abs(P_n_c * P_ft * P_n_ft) > Double.MIN_VALUE)) {
						wordNode.catWeight[nCataCount] += (P_c_ft
								* P_n_c_n_ft - P_n_c_ft * P_c_n_ft)
								* (P_c_ft * P_n_c_n_ft - P_n_c_ft * P_c_n_ft)
								/ (P_ft * P_n_c * P_n_ft);
					}
				} else if (m_paramClassifier.m_nFSMode == ClassifierParam.nFS_WEMode) {
					// Weight of Evielence for text feature selection
					double odds_c_ft;
					double odds_c;
					double P_c_inv_ft = P_c_ft / P_ft;

					if (Math.abs(P_c_inv_ft) < Double.MIN_VALUE)
						odds_c_ft = 1.0 / (N * N - 1);
					else if (Math.abs(P_c_inv_ft - 1) < Double.MIN_VALUE)
						odds_c_ft = N * N - 1;
					else
						odds_c_ft = P_c_inv_ft / (1.0 - P_c_inv_ft);

					if (Math.abs(P_c) < Double.MIN_VALUE)
						odds_c = 1.0 / (N * N - 1);
					else if (Math.abs(P_c - 1) < Double.MIN_VALUE)
						odds_c = N * N - 1;
					else
						odds_c = P_c / (1.0 - P_c);
					if (Math.abs(odds_c) > Double.MIN_VALUE
							&& Math.abs(odds_c_ft) > Double.MIN_VALUE)
						wordNode.catWeight[nCataCount] += P_c * P_ft
								* Math.abs(Math.log(odds_c_ft / odds_c));
				} else // if(m_paramClassifier.m_nFSMode==CClassifierParam::
						// nFS_IGMode)
				{
					// Information gain feature selection
					if ((Math.abs(P_c * P_n_ft) > Double.MIN_VALUE)
							&& (Math.abs(P_c_n_ft) > Double.MIN_VALUE)) {
						wordNode.catWeight[nCataCount] += P_c_n_ft
								* Math.log(P_c_n_ft / (P_c * P_n_ft));
					}
					if ((Math.abs(P_c * P_ft) > Double.MIN_VALUE)
							&& (Math.abs(P_c_ft) > Double.MIN_VALUE)) {
						wordNode.catWeight[nCataCount] += P_c_ft
								* Math.log(P_c_ft / (P_c * P_ft));
					}
				}
				wordNode.m_dWeight += wordNode.catWeight[nCataCount];
				nCataCount++;
			}
			// ASSERT(nCataCount==nTotalCata);
		}
	}

	// ��m_lstWordListѡ�����������Ӽ�,�浽dstWordList��
	public WordList featherSelection(WordList m_lstWordList) {
		if (m_lstWordList.getCount() <= 0)
			return null;
		
		WordList dstWordList = new WordList();
		m_lstWordList.indexWord();

		SortType[] psSortBuf;
		int nDistinctWordNum = m_lstWordList.getCount();

		// the distinct number of the word
		psSortBuf = new SortType[nDistinctWordNum];
		for (int i = 0; i < nDistinctWordNum; ++i) {
			psSortBuf[i] = new SortType();
		}
		long lDocNum = m_lstTrainCatalogList.getDocNum();
		for (int i = 0; i < nDistinctWordNum; i++) {
			psSortBuf[i].pclsWordNode = null;
			psSortBuf[i].dWeight = 0;
		}
		
		// total selecting
		System.out.println("FeatherSelection GolbalMode");
		genSortBuf(m_lstWordList, psSortBuf);// -1 mean sum all catalog
		Arrays.sort(psSortBuf);

		int nSelectWordNum = m_paramClassifier.m_nWordSize;
		if (nSelectWordNum > nDistinctWordNum)
			nSelectWordNum = nDistinctWordNum;

		for (int i = 0; i < nSelectWordNum; i++) {
			WordNode wordNode = new WordNode();
			if (m_paramClassifier.m_nWeightMode == ClassifierParam.nWM_TF_IDF)
				psSortBuf[i].pclsWordNode.computeWeight(lDocNum);
			else if (m_paramClassifier.m_nWeightMode == ClassifierParam.nWM_TF_IDF_DIFF)
				psSortBuf[i].pclsWordNode.computeWeight(lDocNum, true);
			wordNode.m_dWeight = psSortBuf[i].pclsWordNode.m_dWeight;
			wordNode.m_lDocFreq = psSortBuf[i].pclsWordNode.m_lDocFreq;
			wordNode.m_lWordFreq = psSortBuf[i].pclsWordNode.m_lWordFreq;
			dstWordList.put(psSortBuf[i].word, wordNode);
		}
		
		return dstWordList;
	}

	public void genSortBuf(WordList wordList,
							SortType[] psSortBuf) {
		int lWordCount = 0;
		
		// for each word
		for (Entry<String, WordNode> e : wordList.m_lstWordList.entrySet())
		{
			WordNode wordNode = e.getValue();
			String str = e.getKey();
			psSortBuf[lWordCount].pclsWordNode = wordNode;
			psSortBuf[lWordCount].word = str;
			//System.out.println(psSortBuf[lWordCount].dWeight);
			psSortBuf[lWordCount].dWeight = wordNode.m_dWeight;
			
			lWordCount++;
		}
	}

	// ����nType������������ģ�͵����,nType=0����KNN������,nType=1����SVM������
	public boolean WriteModel(String strFileName,WordList m_lstTrainWordList) throws Exception {
		LEDataOutputStream fOut = new LEDataOutputStream(new FileOutputStream(
				strFileName));

		m_lstTrainWordList.dumpToFile(m_paramClassifier.m_txtResultDir
				+ "/features.dat");
		m_lstTrainWordList.dumpWordList(m_paramClassifier.m_txtResultDir
				+ "/features.txt");
		m_lstTrainCatalogList.dumpToFile(m_paramClassifier.m_txtResultDir
				+ "/train.dat", 1);
		m_lstTrainCatalogList.dumpDocList(m_paramClassifier.m_txtResultDir
				+ "/train.txt");
		m_paramClassifier.dumpToFile(m_paramClassifier.m_txtResultDir
				+ "/params.dat");

		docSVM.param.classifier_num = m_lstTrainCatalogList.getCataNum();
		docSVM.param.trainfile = "train.txt";
		docSVM.param.resultpath = m_paramClassifier.m_txtResultDir;
		docSVM.param.DumpToFile(m_paramClassifier.m_txtResultDir
				+ "/svmparams.dat");

		fOut.writeInt(dwModelFileID);
		fOut.writeString("params.dat");
		fOut.writeString("features.dat");
		fOut.writeString("train.dat");
		fOut.writeString("svmparams.dat");

		fOut.close();
		return true;
	}
}
