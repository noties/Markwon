# Editor

Markdown editor for Android based on `Markwon`.

Main principle: _difference_ between input text and rendered markdown is considered to be
_punctuation_.


[https://noties.io/Markwon/docs/v4/editor/](https://noties.io/Markwon/docs/v4/editor/)


## Limitations

Tables and LaTeX nodes won't be rendered correctly. They will be treated as _punctuation_
as whole. This comes from their implementation - they are _mocked_ and do not present
in final result as text and thus cannot be _diffed_. 