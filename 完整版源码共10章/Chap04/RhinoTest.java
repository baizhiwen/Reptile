

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;


public class RhinoTest
{
    public static void main(String[] args)
    {
        /* ����һ��Javascript�������Ļ����������洢Javascript�Ļ�����Ϣ */
        Context cx = Context.enter();
        try {
            /* ��ʼ��Javascript��׼��������Object, Function, Array�ȣ� */
            Scriptable scope = cx.initStandardObjects();
 
            /* ��ȡһ��.js�ļ� */
            String script = "";
            File file = null;
            if(args.length > 0)
            {
                file = new File(args[0]);  // ����в�����������һ��������ָ����js�ļ�
            }
            else
            {
                file = new File("script.js"); // ���û�в����������script.js
            }
            BufferedReader in = new BufferedReader(new FileReader(file));
            String s = "";
            while((s = in.readLine()) != null)
            {
                script += s + "\n";
            }
 
            /* ִ�д��� */
            cx.evaluateString(scope, script, "[" + file.getName() + "]", 1, null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            Context.exit();
        }
    }
}

