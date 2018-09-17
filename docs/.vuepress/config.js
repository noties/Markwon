module.exports = {
    base: '/Markwon/',
    title: 'Markwon',
    description: 'Android markdown library based on commonmark specification',
    head: [
        ['link', {rel: 'apple-touch-icon', sizes: '180x180', href: '/apple-touch-icon.png?v=1'}],
        ['link', {rel: 'icon', type: 'image/png', sizes: '16x16', href: '/favicon-16x16.png?v=1'}],
        ['link', {rel: 'icon', href: '/favicon.ico?v=1'}],
        ['link', {rel: 'icon', type: 'image/png', sizes: '32x32', href: '/favicon-32x32.png?v=1'}],
        ['link', {rel: 'manifest', href: '/manifest.json?v=1'}],
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
        ],
        sidebarDepth: 2,
        lastUpdated: true
    },
    markdown: {
        config: md => {
            md.use(require('markdown-it-task-lists'));
        }
    }
}