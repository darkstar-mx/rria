package mx.grailscoder.error

import org.codehaus.groovy.GroovyException
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ModuleNode

/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya [ at ] outlook.com
 * @Date 5/20/2017

 *
 */
class BusinessException extends GroovyRuntimeException{

    private ModuleNode module;
    private ASTNode node;

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, ASTNode node) {
        super(message);
        this.node = node;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable t) {
        super();
        initCause(t);
    }

    public void setModule(ModuleNode module) {
        this.module = module;
    }

    public ModuleNode getModule() {
        return module;
    }

    public String getMessage() {
        return getMessageWithoutLocationText() + getLocationText();
    }

    public ASTNode getNode() {
        return node;
    }

    public String getMessageWithoutLocationText() {
        return super.getMessage();
    }

    protected String getLocationText() {
        String answer = ". ";
        if (node != null) {
            answer += "At [" + node.getLineNumber() + ":" + node.getColumnNumber() + "] ";
        }
        if (module != null) {
            answer += module.getDescription();
        }
        if (answer.equals(". ")) {
            return "";
        }
        return answer;
    }
}
