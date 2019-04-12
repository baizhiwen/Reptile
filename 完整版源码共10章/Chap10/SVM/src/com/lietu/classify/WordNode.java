package com.lietu.classify;

public class WordNode {
	static final double dZero = 1.0E-10;
	
	//	������ѡ���ʱ����������������������ֶ�
	//������ѡ�����֮������������ÿ��������"log(ѵ���ĵ�����/�������ĵ�Ƶ��)"ֵ
	public float m_dWeight;
	public int m_nAllocLen;
	public int m_nWordID;           //������ID
	public double[] catWeight = null;   //��������ÿ���������ֶ�
	//�����������ĵ����е��ĵ�Ƶ��,ʵ�ʾ��Ǻ���GetDocNum()���ص�ֵ
	//����������Ǵ�ѵ���ĵ�����ѡ��õ��ģ����޷�ʹ��GetDocNum()�õ��������ĵ�Ƶ��
	//���ԣ��˴�ʹ��m_lDocFreq����¼�������ĵ�Ƶ��
	public int m_lDocFreq;         //�����������ĵ����е��ĵ�Ƶ��
	//�����������ĵ����еĴ�Ƶ,ʵ�ʾ��Ǻ���GetWordNum()���ص�ֵ
	//����������Ǵ�ѵ���ĵ�����ѡ��õ��ģ����޷�ʹ��GetWordNum()�õ������Ĵ�Ƶ
	//���ԣ��˴�ʹ��m_lWordFreq����¼�����Ĵ�Ƶ
	public int m_lWordFreq;        //�����������ĵ����еĴ�Ƶ
	public long[] m_pCataDocFreq = null;    //������ÿһ������е��ĵ�Ƶ��
	public long[] m_pCataWordFreq = null;   //������ÿһ������еĴ�Ƶ
	public long m_lDocID;           //�õ��������ĵ�Ƶ�ʵ�ʱ���õ�
	
	public WordNode()
	{
		m_dWeight=0.0f;
		m_nAllocLen=0;
		catWeight=null;
		m_pCataDocFreq=null;
		m_pCataWordFreq=null;
		m_lDocFreq=0;
		m_lWordFreq=0;
		m_nWordID=-1;
		m_lDocID=-1;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("m_nWordID:");
		sb.append(m_nWordID);
		sb.append('\n');
		
		sb.append("m_dWeight:");
		sb.append(m_dWeight);
		sb.append('\n');

		sb.append("m_lDocFreq:");
		sb.append(m_lDocFreq);
		sb.append('\n');

		sb.append("m_lWordFreq:");
		sb.append(m_lWordFreq);
		sb.append('\n');

		sb.append("catWeight:");
		for(double cw:catWeight)
		{
			sb.append(cw);
			sb.append(" ");
		}
		sb.append('\n');
		
		sb.append("m_pCataDocFreq:");
		for(long cw:m_pCataDocFreq)
		{
			sb.append(cw);
			sb.append(" ");
		}
		sb.append('\n');

		sb.append("m_pCataWordFreq:");
		for(long cw:m_pCataWordFreq)
		{
			sb.append(cw);
			sb.append(" ");
		}
		sb.append('\n');
		
		return sb.toString();
	}
	
	public void initBuffer(int nLen)
	{
		if(nLen<=0) return;
		if(m_nAllocLen<=0&&catWeight==null&&
			m_pCataDocFreq==null&&m_pCataWordFreq==null)
		{
			m_nAllocLen=nLen;
			catWeight=new double[m_nAllocLen];
			//for(int i=0;i<m_nAllocLen;++i)
			//{
			//	catWeight[i]=0;
			//}
			m_pCataDocFreq=new long[m_nAllocLen];

			//for(int i=0;i<m_nAllocLen;++i)
			//{
			//	m_pCataDocFreq[i]=0;
			//}
			m_pCataWordFreq=new long[m_nAllocLen];

			//for(int i=0;i<m_nAllocLen;++i)
			//{
			//	m_pCataWordFreq[i]=0;
			//}
		}
	}

	//	�˺�������ֻ�ڲ�η������õ�,�������ƺ���ʵ�ֵĹ��ܿ������е㲻ͬ
	public void copy(WordNode wordNode)
	{
		m_dWeight=wordNode.m_dWeight;
		m_nAllocLen=0;
		m_nWordID=wordNode.m_nWordID;
		m_lDocFreq=wordNode.m_lDocFreq;
		catWeight=null;
		m_pCataDocFreq=null;
		m_pCataWordFreq=null;
		m_lDocID=0;
	}
	
	public long getWordNum()
	{
		long sum=0;
		if(m_nAllocLen>0)
		{
			for(int i=0;i<m_nAllocLen;i++)
				sum+=m_pCataWordFreq[i];
		}
		else sum=m_lWordFreq;
		return sum;
	}
	
	public long getDocNum()
	{
		long sum=0;
		if(m_nAllocLen>0)
		{
			for(int i=0;i<m_nAllocLen;i++)
				sum+=m_pCataDocFreq[i];
		}
		else sum=m_lDocFreq;
		return sum;
	}
	
	public long getCataWordNum(int cataID)
	{
		return m_pCataWordFreq[cataID];	
	}
	
	public long getCataDocNum(int cataID)
	{
		long sum=0;
		if(m_nAllocLen>0)
		{
			for(int i=0;i<m_nAllocLen;i++)
				sum+=m_pCataDocFreq[i];
		}
		else sum=m_lDocFreq;
		return sum;
	}
	
	//	���ڼ���������Ȩ�أ�����sum�����ĵ����е��ĵ�����
	//	���bMult=true��m_dWeight����0, �������ķ����ĵ�Ƶ�ʳ���m_dWeightԭ����ֵ, �ٱ��浽��Ա����m_dWeight��
	//	����, �������ķ����ĵ�Ƶ��ֵ���浽��Ա����m_dWeight��
	public void computeWeight(long sum, boolean bMult)
	{
		long docFreq=getDocNum();
		if(docFreq<=0&&sum<=0)
		{
			m_dWeight=0.0f;
			return;
		}
		float weight=(float)Math.log((double)sum/(double)docFreq);
		if(bMult&&m_dWeight>dZero)
			m_dWeight*=weight;
		else
			m_dWeight=weight;
	}
	
	public void computeWeight(long sum)
	{
		computeWeight(sum, false);
	}
}
