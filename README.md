
> The content of this repo is inspirational work
> originated from https://medium.com/@klintcho/training-a-swedish-pos-tagger-for-stanford-corenlp-546e954a8ee7

## Preparations

1. Download (Talbanken treebank) https://cl.lingfil.uu.se/~nivre/swedish_treebank/talbanken-stanford-1.2.tar.gz TAR.
2. Extract the GZ-file (contains a TAR-file)
3. Extract the TAR-file
4. Place the two (2) files under ``./copyrighted/training/``
> ``talbanken-stanford-test.conll``
> ``talbanken-stanford-train.conll``


## Glossary

| Title                           | Desc                                                                   |
|---------------------------------|------------------------------------------------------------------------|
| Dependency tree bank            | Specific for the language of interest. Typically represented as CoNLL  |
| Embeddings file                 | Word embeddings for words in the language of interest.                 |
| TreebankLanguagePack (TLP file) | Describes properties of the specific treebank file and language        |
| CoNLL                           | A standard format used for annotated data in a range of NLP tasks.     |

## Other
Other interesting post
https://medium.com/@klintcho/training-a-swedish-dependency-parse-model-in-stanford-corenlp-7a1ba1a412c4
