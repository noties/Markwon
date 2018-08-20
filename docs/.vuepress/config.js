module.exports = {
    base: '/Markwon/',
    title: 'Markwon',
    description: 'Android markdown library based on commonmark specification',
    head: [
        ['link', {rel: 'icon', href: '/favicon.png'}]
    ],
    themeConfig: {
        nav: [
            { text: 'Install', link: '/docs/install.md' },
            { text: 'Github', link: 'https://github.com/noties/Markwon' }
        ],
        sidebar: [
            '/',
            '/docs/getting-started.md',
            '/docs/configure.md',
            '/docs/html.md',
            '/docs/syntax-highlight.md',
            '/docs/image-loader.md',
            '/docs/view.md'
        ]
    }
}