/*
 * Created on 2005-2-9
 *
 */
package test.classify;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;

import com.lietu.seg.result.CnToken;
import com.lietu.seg.result.WordIndex;

/**
 * @author luogang
 *
 */
public class TaggerTest {

	public static void main(String[] args) throws Exception
	{
		//testNormalSeg();
		//testOrg();
		//testPlace();
		//testPOS();
		//testBrand();
		testFormatSeg();
		//testSplit();
		//testSplitWords();
	}
	
    public static void testFormatSeg() throws Exception{
    	long startTime;
    	long endTime;
    	
    	com.lietu.seg.result.Tagger.makeTag= true;//false;
    	com.lietu.seg.result.Tagger.segSZ = false;
    	String sentence =//"�������ϣ����������";
    		//"�й�������͵˰��";
    		"�ڹ��ݷֱ�����UPS����ϵ�Դ����ά�������������ء�";
    		//"�Ϻ���������Ƽ�����... ��˾��  ���Ϻ���������Ƽ����޹�˾��һ�Ҵ���ͨ����������Ĺ�˾��Ϊ��Ӧ�г��ĸ��ٷ�չ����֯�и���Ǳ�����Ŷӣ��ֳ�Ƹ��������飬�����ɾ�������ս���ܳ��ܿ��ٷ�չѹ��������ſ����ˡ��������ޣ�Ψ�����á� ��ϵ��ʽ�� �� ����021-36030126 E-mail��zhujc@linghui.com ְλ1�� �����������ʦ ְλ������ JAVA�������ʦ��3-4�ˣ����Σ�����java���������빤����";//"���а����˰���ȫ�ݡ��������˰���ȫ������ɳ�ӡ���˷��ƽ�֡���������˷�������й����������鴨�������Ρ�ƽ������ɽ���������������ٹ�·��";
    	//"��ɺ�Ȩ��";
    		//"����ƶ�";
    	//seg.result.Tagger tagger = new seg.result.Tagger();
    	ArrayList result = com.lietu.seg.result.Tagger.getFormatSegResult(sentence);
    	
    	startTime = System.currentTimeMillis();
    	for (int i=0; i<result.size();i++)
    	{
    		CnToken t = (CnToken)result.get(i);
            System.out.println(t.termText() + " " + t.startOffset() + " "
                               + t.endOffset() + " "+t.type());
        }
    	endTime = System.currentTimeMillis();
    	System.out.println("first seg time cost:" + ( endTime - startTime));
    }

    /*public static void testSplitWords() throws Exception{
    	long startTime;
    	long endTime;
    	
    	StringBuffer sentence =//"�������ϣ����������";
    		//"�й�������͵˰��";
    		new StringBuffer("�Ϻ���������Ƽ�����... ��˾��  ���Ϻ���������Ƽ����޹�˾��һ�Ҵ���ͨ����������Ĺ�˾��Ϊ��Ӧ�г��ĸ��ٷ�չ����֯�и���Ǳ�����Ŷӣ��ֳ�Ƹ��������飬�����ɾ�������ս���ܳ��ܿ��ٷ�չѹ��������ſ����ˡ��������ޣ�Ψ�����á� ��ϵ��ʽ�� �� ����021-36030126 E-mail��zhujc@linghui.com ְλ1�� �����������ʦ ְλ������ JAVA�������ʦ��3-4�ˣ����Σ�����java���������빤����");//"���а����˰���ȫ�ݡ��������˰���ȫ������ɳ�ӡ���˷��ƽ�֡���������˷�������й����������鴨�������Ρ�ƽ������ɽ���������������ٹ�·��";
    	//"��ɺ�Ȩ��";
    		//"����ƶ�";
    	int offset = 0;
    	SortedSet<WordIndex> result = seg.result.Tagger.splitWords(sentence,offset,sentence.length() - offset);
    	
    	startTime = System.currentTimeMillis();
    	
    	Iterator<WordIndex> iterator = result.iterator();
        while (iterator.hasNext())
        {
        	WordIndex item = iterator.next();
            System.out.print( item .word() + " " + item.index() + "\n" );
        }

    	//for (int i=0; i<result.size();i++)
    	//{
        //    System.out.println(result.);
        //}
    	endTime = System.currentTimeMillis();
    	System.out.println("first seg time cost:" + ( endTime - startTime));
    }*/
    
	public static void testNormalSeg() throws Exception
	{
		//seg.result.Tagger tagger = new seg.result.Tagger();
		
		//seg.result.Tagger.makeTag = false;
		com.lietu.seg.result.Tagger.segName = true;
		
		String sSentence="ȫ���޽��е�������ǰ¼ȡ��֮һ��ÿ��4.5����Ԫ��ȫ�ѧ������---�������и���Ů����õ�ݻ���˹����ѧ���ź����ѧ������";
		String sSentenceResult;
		
		long startTime = System.currentTimeMillis();
		sSentenceResult= com.lietu.seg.result.Tagger.getNormalSegResult(sSentence);
		System.out.println("seg time cost:" + (System.currentTimeMillis() - startTime)); 
		
		System.out.println(sSentenceResult);
		
		sSentence="��׼�ƶ��ļҵ糧�̣�";//"��������̨������ͣ��൱�͵�";
		sSentenceResult= com.lietu.seg.result.Tagger.getNormalSegResult(sSentence);
		System.out.println(sSentenceResult);
		
		sSentence="�»�����߻�����";
		sSentenceResult= com.lietu.seg.result.Tagger.getNormalSegResult(sSentence);
		System.out.println(sSentenceResult);
		
		sSentence="���ҺͲ��ٳ��ɵش�";//"���ܡ�������Ҧѩ�����ɡ�����ס��Ź��ꡢ���롢κΡ�����ݡ�����褡���ѧ�ĵ����ս�֪����ʿ�Ⱥ��ԡ�";
		sSentenceResult= com.lietu.seg.result.Tagger.getNormalSegResult(sSentence);
		System.out.println(sSentenceResult);
	}
	
	
	public static void testPlace() throws Exception
	{
		//seg.result.Tagger tagger = new seg.result.Tagger();

		//seg.result.Tagger.makeTag = true;
		com.lietu.seg.result.Tagger.segSZ = false;
		
		String sSentence="�л����񹲺͹���ͨ�ߵ�ѧУ�������ջ��ȡ��۰ġ�̨��ʡѧ���칫�ң�����й������а죩�����ڹ㶫ʡ�������ڣ�";
		String sSentenceResult;

		long startTime = System.currentTimeMillis();
		sSentenceResult = com.lietu.seg.result.Tagger.getNormalSegResult(sSentence);
		//141
		System.out.println("seg time cost:" + (System.currentTimeMillis() - startTime)); 
		System.out.println(sSentenceResult);
	}

	public static void testOrg() throws Exception
	{
		//seg.result.Tagger tagger = new seg.result.Tagger();

		//seg.result.Tagger.makeTag = true;
		com.lietu.seg.result.Tagger.segSZ = false;
		
		String sSentence="�����г���,�������մ����۴�����";
		String sSentenceResult;

		long startTime = System.currentTimeMillis();
		sSentenceResult = com.lietu.seg.result.Tagger.getNormalSegResult(sSentence);
		com.lietu.seg.result.Tagger.reLoad();
		com.lietu.seg.result.Tagger.getFormatSegResult(sSentence);
		//141
		System.out.println("seg time cost:" + (System.currentTimeMillis() - startTime)); 
		System.out.println(sSentenceResult);
	}
}
