package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.resource.StereotypedClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.DogController;

public class VRaptor2PathResolverTest {

    private Mockery mockery;
    private ResourceMethod method;
    private StereotypedClass resource;
    private VRaptor2PathResolver resolver;
    private Config config;
    private HttpServletRequest request;
	private MethodInfo info;

    @Before
    public void config() {
        this.mockery = new Mockery();
        this.method = mockery.mock(ResourceMethod.class);
        this.resource = mockery.mock(StereotypedClass.class);
        this.config = mockery.mock(Config.class);
        this.request = mockery.mock(HttpServletRequest.class);
        this.info =mockery.mock(MethodInfo.class); 
        mockery.checking(new Expectations() {
            {
                one(config).getViewPattern(); will(returnValue("/$component/$logic.$result.jsp"));
            }
        });
        this.resolver = new VRaptor2PathResolver(config, request, info);
    }

    @Test
    public void shouldDelegateToVraptor3IfItsNotAVRaptor2Component() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                exactly(2).of(method).getResource();
                will(returnValue(resource));
                one(method).getMethod();
                will(returnValue(DogController.class.getDeclaredMethod("bark")));
                exactly(2).of(resource).getType();
                will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue(null));
            }
        });
        String result = resolver.pathFor(method);
        assertThat(result, is(equalTo("/WEB-INF/jsp/dog/bark.jsp")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseVRaptor2AlgorithmIfAVRaptor2Component() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	one(info).getResult(); will(returnValue("ok"));
                one(method).getResource();
                will(returnValue(resource));
                one(method).getMethod();
                will(returnValue(CowLogic.class.getDeclaredMethod("eat")));
                exactly(2).of(resource).getType();
                will(returnValue(CowLogic.class));
            }
        });
        String result = resolver.pathFor(method);
        assertThat(result, is(equalTo("/cow/eat.ok.jsp")));
        mockery.assertIsSatisfied();
    }

}
