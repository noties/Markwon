module.exports = {
    base: '/Markwon/',
    title: 'Markwon',
    description: 'Android markdown library based on commonmark specification',
    head: [
        ['link', { rel: 'apple-touch-icon', sizes: '180x180', href: '/apple-touch-icon.png?v=1' }],
        ['link', { rel: 'icon', type: 'image/png', sizes: '16x16', href: '/favicon-16x16.png?v=1' }],
        ['link', { rel: 'icon', href: '/favicon.ico?v=1' }],
        ['link', { rel: 'icon', type: 'image/png', sizes: '32x32', href: '/favicon-32x32.png?v=1' }],
        ['link', { rel: 'manifest', href: '/manifest.json?v=1' }],
    ],
    themeConfig: {
        nav: [
            { text: 'Install', link: '/docs/install.md' },
            { text: 'Changelog', link: '/CHANGELOG.md' },
            { text: 'Sandbox', link: '/sandbox.md' },
            { text: 'Github', link: 'https://github.com/noties/Markwon' }
        ],
        sidebar: {
            '/docs/v2/': [
                'install.md',
                'getting-started.md',
                'configure.md',
                'theme.md',
                'factory.md',
                'image-loader.md',
                'syntax-highlight.md',
                'html.md',
                'view.md'
            ],
            '/': [
                '',
                {
                    title: 'Core',
                    children: [
                        '/docs/core/getting-started.md',
                        '/docs/core/theme.md'
                    ]
                },
                '/docs/ext-latex/latex.md',
                '/docs/ext-strikethrough/strikethrough.md',
                '/docs/ext-tables/tables.md',
                '/docs/ext-tasklist/tasklist.md',
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
                '/docs/recycler/recycler.md',
                '/docs/syntax-highlight/syntax-highlight.md',
                '/docs/migration-2-3.md',
                ['/docs/v2/install.md', 'Legacy 2.x.x documentation']
            ]
        },
        sidebarDepth: 2,
        lastUpdated: true
    },
    markdown: {
        config: md => {
            md.use(require('markdown-it-task-lists'));
        }
    }
}