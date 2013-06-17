package freemarker.ext.jsp;

import freemarker.template.TemplateModelException;

/**
 * @author Attila Szegedi
 * @version $Id: FreeMarkerPageContext1.java,v 1.1.2.1 2006/07/08 14:45:34 ddekany Exp $
 */
class FreeMarkerPageContext1 extends FreeMarkerPageContext {

    private FreeMarkerPageContext1() throws TemplateModelException {
        super();
    }

    static FreeMarkerPageContext create() throws TemplateModelException {
        return new FreeMarkerPageContext1();
    }

    public void include (String s, boolean b) {}

 }
