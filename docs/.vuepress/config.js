module.exports = {
    base: '/Markwon/',
    title: 'Markwon',
    description: 'Android markdown library based on commonmark specification that renders markdown as system-native Spannables (no WebView)',
    head: [
        ['link', { rel: 'apple-touch-icon', sizes: '180x180', href: '/apple-touch-icon.png?v=1' }],
        ['link', { rel: 'icon', type: 'image/png', sizes: '16x16', href: '/favicon-16x16.png?v=1' }],
        ['link', { rel: 'icon', href: '/favicon.ico?v=1' }],
        ['link', { rel: 'icon', type: 'image/png', sizes: '32x32', href: '/favicon-32x32.png?v=1' }],
        ['link', { rel: 'manifest', href: '/manifest.json?v=1' }],
        ['meta', { name: 'keywords', content: 'android,markdown,library,spannable,markwon,commonmark' }]
    ],
    themeConfig: {
        nav: [
            { text: 'Install', link: '/docs/install.md' },
            { text: 'Changelog', link: '/CHANGELOG.md' },
            {
                text: 'API Version',
                items: [
                    { text: 'Current (3.x.x)', link: '/' },
                    { text: 'Legacy (2.x.x)', link: '/docs/v2/' }
                ]
            },
            { text: 'Sandbox', link: '/sandbox.md' },
            { text: 'Donate', link: '/donate.md' },
            { text: 'Github', link: 'https://github.com/noties/Markwon' }
        ],
        sidebar: {
            '/docs/v2/': [
                '',
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
                    collapsable: false,
                    children: [
                        '/docs/core/getting-started.md',
                        '/docs/core/plugins.md',
                        '/docs/core/theme.md',
                        '/docs/core/images.md',
                        '/docs/core/configuration.md',
                        '/docs/core/visitor.md',
                        '/docs/core/spans-factory.md',
                        '/docs/core/html-renderer.md',
                        '/docs/core/core-plugin.md',
                        '/docs/core/movement-method-plugin.md'
                    ]
                },
                '/docs/ext-latex/',
                '/docs/ext-strikethrough/',
                '/docs/ext-tables/',
                '/docs/ext-tasklist/',
                {
                    title: 'HTML',
                    collapsable: false,
                    children: [
                        '/docs/html/',
                        '/docs/html/custom-tag-handler.md'
                    ]
                },
                '/docs/image/gif.md',
                '/docs/image/okhttp.md',
                '/docs/image/svg.md',
                '/docs/recycler/',
                '/docs/syntax-highlight/',
                '/docs/migration-2-3.md'
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