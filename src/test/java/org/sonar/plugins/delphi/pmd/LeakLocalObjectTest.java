package org.sonar.plugins.delphi.pmd;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class LeakLocalObjectTest extends BasePmdRuleTest {
    @Test
    public void validRule() {
        DelphiUnitBuilderTest builder = new DelphiUnitBuilderTest();
        builder.appendImpl("" +
                "procedure GetUserText;\n" +
                "var localList : TStringList;\n" +
                "begin\n" +
                "  localList := TStringList.Create;\n" +
                "    begin\n" +
                "        result := result + ' ' + curStr;\n" +
                "    end;\n" +
                "    localList.Free;\n" +
                "end;\n" +
                "\n" +
                "function TNalogForm.oiParamsProperties0CloseUpList: Boolean;\n" +
                "var list: TList;\n" +
                "begin\n" +
                "  list := TList.Create;\n" +
                "  sleep(0);\n" +
                "  list.Destroy;\n" +
                "end;\n" +
                "");

        analyse(builder);

        assertThat(issues.toString(), issues, hasSize(1)); // DestroyRule in results
    }

    @Test
    public void LeakLocalObjectIssue() {
        DelphiUnitBuilderTest builder = new DelphiUnitBuilderTest();
        builder.appendImpl("" +
                        "  function GetRightCommaText(Text: String): String;\n" +
                        "  var\n" +
                        "    slText : TStringList;\n" +
                        "    i      : Integer; \n" +
                        "  begin\n" +
                        "    slText := TStringList.Create;\n" +
                        "    try\n" +
                        "      slText.CommaText := Text;\n" +
                        "          Result := Result + ',' + slText[i];\n" +
                        "    finally\n" +
                        "      Result := slText.CommaText;\n" +
                        "    end;\n" +
                        "  end;\n" +
                "");

        analyse(builder);

        assertThat(issues.toString(), issues, hasSize(1));
    }


    @Test
    public void LeakLocalObjectIssue2() {
        DelphiUnitBuilderTest builder = new DelphiUnitBuilderTest();
        builder.appendImpl("" +
                "procedure ParseParams;\n" +
                "\n" +
                "  procedure ParseExtra;\n" +
                "  begin\n" +
                "    beep1;\n" +
                "  end;\n" +
                "\n" +
                "begin\n" +
                "  beep2;\n" +
                "end;\n" +
                "\n" +
                "procedure ClsEditButtonClick;\n" +
                "\n" +
                "  function GetRightCommaText(Text: String): String;\n" +
                "  var\n" +
                "    slText : TStringList;\n" +
                "    i      : Integer; \n" +
                "  begin\n" +
                "    slText := TStringList.Create;\n" +
                "    slText.Text := Text;" +
                "      Result := slText.CommaText;\n" +
                "  end;\n" +
                "\n" +
                "begin\n" +
                "  beep3;\n" +
                "end;\n" +
                "");

        analyse(builder);

        assertThat(issues.toString(), issues, hasSize(1));
    }

}
