package io.noties.markwon.editor;

import android.text.Editable;
import android.widget.EditText;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutorService;

import io.noties.markwon.editor.MarkwonEditor.PreRenderResult;
import io.noties.markwon.editor.MarkwonEditor.PreRenderResultListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonEditorTextWatcherTest {

    @Test
    public void w_process() {

        final MarkwonEditor editor = mock(MarkwonEditor.class);
        final Editable editable = mock(Editable.class);

        final MarkwonEditorTextWatcher watcher = MarkwonEditorTextWatcher.withProcess(editor);

        watcher.afterTextChanged(editable);

        verify(editor, times(1)).process(eq(editable));
    }

    @Test
    public void w_pre_render() {

        final MarkwonEditor editor = mock(MarkwonEditor.class);
        final Editable editable = mock(Editable.class);
        final ExecutorService service = mock(ExecutorService.class);
        final EditText editText = mock(EditText.class);

        when(editable.getSpans(anyInt(), anyInt(), any(Class.class))).thenReturn(new Object[0]);

        when(editText.getText()).thenReturn(editable);

        when(service.submit(any(Runnable.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((Runnable) invocation.getArgument(0)).run();
                return null;
            }
        });

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((Runnable) invocation.getArgument(0)).run();
                return null;
            }
        }).when(editText).post(any(Runnable.class));

        final MarkwonEditorTextWatcher watcher = MarkwonEditorTextWatcher.withPreRender(
                editor,
                service,
                editText);

        watcher.afterTextChanged(editable);

        final ArgumentCaptor<PreRenderResultListener> captor =
                ArgumentCaptor.forClass(PreRenderResultListener.class);

        verify(service, times(1)).submit(any(Runnable.class));
        verify(editor, times(1)).preRender(any(Editable.class), captor.capture());

        final PreRenderResultListener listener = captor.getValue();
        final PreRenderResult result = mock(PreRenderResult.class);

        // for simplicity return the same editable instance (same hashCode)
        when(result.resultEditable()).thenReturn(editable);

        listener.onPreRenderResult(result);

        // if we would check for hashCode then this method would've been invoked
//        verify(result, times(1)).resultEditable();
        verify(result, times(1)).dispatchTo(eq(editable));
    }

    @Test
    public void pre_render_posts_exception_to_main_thread() {

        final RuntimeException e = new RuntimeException();

        final MarkwonEditor editor = mock(MarkwonEditor.class);
        final ExecutorService service = mock(ExecutorService.class);
        final EditText editText = mock(EditText.class, RETURNS_MOCKS);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                throw e;
            }
        }).when(editor).preRender(any(Editable.class), any(PreRenderResultListener.class));

        when(service.submit(any(Runnable.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((Runnable) invocation.getArgument(0)).run();
                return null;
            }
        });

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);

        final MarkwonEditorTextWatcher textWatcher =
                MarkwonEditorTextWatcher.withPreRender(editor, service, editText);

        textWatcher.afterTextChanged(mock(Editable.class, RETURNS_MOCKS));

        verify(editText, times(1)).post(captor.capture());

        try {
            captor.getValue().run();
            fail();
        } catch (Throwable t) {
            assertEquals(e, t.getCause());
        }
    }
}
