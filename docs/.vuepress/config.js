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
            { text: 'Changelog', link: '/CHANGELOG.md' },
            { text: 'Github', link: 'https://github.com/noties/Markwon' }
        ],
        sidebar: [
            '/',
            '/docs/getting-started.md',
            '/docs/configure.md',
            '/docs/theme.md',
            '/docs/factory.md',
            '/docs/image-loader.md',
            '/docs/syntax-highlight.md',
            '/docs/html.md',
            '/docs/view.md'
        ]
    }
}