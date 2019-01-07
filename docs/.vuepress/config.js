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
            {
                title: 'Core',
                children: [
                    '/docs/core/getting-started.md'
                ]
            },
            {
                title: 'LaTeX extension',
                children: [
                    '/docs/ext-latex/latex.md'
                ]
            },
            {
                title: 'Strikethrough extension',
                children: [
                    '/docs/ext-strikethrough/strikethrough.md'
                ]
            },
            {
                title: 'Tables extension',
                children: [
                    '/docs/ext-tables/tables.md'
                ]
            },
            {
                title: 'Task list extension',
                children: [
                    '/docs/ext-tasklist/tasklist.md'
                ]
            },
            {
                title: 'HTML',
                children: [
                    '/docs/html/html.md'
                ]
            },
            {
                title: 'Image',
                children: [
                    '/docs/image/gif.md',
                    '/docs/image/okhttp.md',
                    '/docs/image/svg.md'
                ]
            },
            {
                title: 'Recycler',
                children: [
                    '/docs/recycler/recycler.md'
                ]
            },
            {
                title: 'Syntax highlight',
                children: [
                    '/docs/syntax-highlight/syntax-highlight.md'
                ]
            },
            '/docs/migration-2-3.md'

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