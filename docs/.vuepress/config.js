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
            { text: 'Install', link: '/docs/v3/install.md' },
            { text: 'Changelog', link: '/CHANGELOG.md' },
            {
                text: 'API Version',
                items: [
                    { text: 'Current (3.x.x)', link: '/' },
                    { text: 'Beta (4.x.x)', link: '/docs/v4/install.md' },
                    { text: 'Legacy (2.x.x)', link: '/docs/v2/' }
                ]
            },
            { text: 'Github', link: 'https://github.com/noties/Markwon' }
        ],
        sidebar: {
            '/docs/v2': [
                '/docs/v2/getting-started.md',
                '/docs/v2/install.md',
                '/docs/v2/configure.md',
                '/docs/v2/theme.md',
                '/docs/v2/factory.md',
                '/docs/v2/image-loader.md',
                '/docs/v2/syntax-highlight.md',
                '/docs/v2/html.md',
                '/docs/v2/view.md'
            ],
            '/docs/v4': [
                '/docs/v4/install.md',
                {
                    title: 'Core',
                    collapsable: false,
                    children: [
                        '/docs/v4/core/getting-started.md',
                        '/docs/v4/core/plugins.md',
                        '/docs/v4/core/registry.md',
                        '/docs/v4/core/theme.md',
                        '/docs/v4/core/configuration.md',
                        '/docs/v4/core/visitor.md',
                        '/docs/v4/core/spans-factory.md',
                        '/docs/v4/core/core-plugin.md',
                        '/docs/v4/core/movement-method-plugin.md',
                        '/docs/v4/core/render-props.md'
                    ]
                },
                '/docs/v4/ext-latex/',
                '/docs/v4/ext-strikethrough/',
                '/docs/v4/ext-tables/',
                '/docs/v4/ext-tasklist/',
                '/docs/v4/html/',
                '/docs/v4/image/',
                '/docs/v4/image-glide/',
                '/docs/v4/image-picasso/',
                '/docs/v4/recycler/',
                '/docs/v4/recycler-table/',
                '/docs/v4/syntax-highlight/',
                '/docs/v4/recipes.md',
                '/docs/v4/migration-3-4.md'
            ],
            '/': [
                '',
                {
                    title: 'Core',
                    collapsable: false,
                    children: [
                        '/docs/v3/core/getting-started.md',
                        '/docs/v3/core/plugins.md',
                        '/docs/v3/core/theme.md',
                        '/docs/v3/core/images.md',
                        '/docs/v3/core/configuration.md',
                        '/docs/v3/core/visitor.md',
                        '/docs/v3/core/spans-factory.md',
                        '/docs/v3/core/html-renderer.md',
                        '/docs/v3/core/core-plugin.md',
                        '/docs/v3/core/movement-method-plugin.md',
                        '/docs/v3/core/render-props.md'
                    ]
                },
                '/docs/v3/ext-latex/',
                '/docs/v3/ext-strikethrough/',
                '/docs/v3/ext-tables/',
                '/docs/v3/ext-tasklist/',
                '/docs/v3/html/',
                '/docs/v3/image/gif.md',
                '/docs/v3/image/okhttp.md',
                '/docs/v3/image/svg.md',
                '/docs/v3/recycler/',
                '/docs/v3/recycler-table/',
                '/docs/v3/syntax-highlight/',
                '/docs/v3/migration-2-3.md'
            ]
        },
        sidebarDepth: 2,
        lastUpdated: true
    }
}